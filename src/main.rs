#![allow(non_snake_case)]
#![windows_subsystem = "windows"]

mod error;

use std::fs;
use std::path::Path;
use std::process::{ExitCode, Termination};
use jni::{InitArgs, InitArgsBuilder, JavaVM, JNIVersion};
use jni::objects::JValue;
use serde::Deserialize;
use winapi::um::wincon::{ATTACH_PARENT_PROCESS, AttachConsole};
use crate::error::Error;

type Result<T> = std::result::Result<T, Error>;

fn main() -> ExitCode {
    unsafe {
        AttachConsole(ATTACH_PARENT_PROCESS);
    }

    match run() {
        Ok(_) => ExitCode::from(0),
        Err(err) => {
            eprintln!("{}", err);
            err.report()
        }
    }
}

fn run() -> Result<()> {
    let current_exe = std::env::current_exe().map_err(Error::UnknownExecutable)?;
    let application_dir = current_exe.parent().ok_or(Error::UnknownApplicationDir)?;

    let config: Config = load_config(&application_dir.join("config.toml"))?;
    let init_args = build_init_args(&config)?;

    let libjvm_path = application_dir.join(&config.libjvm_path);
    let jvm = JavaVM::with_libjvm(init_args, || Ok(libjvm_path.as_os_str()))
        .map_err(|err| {
            eprintln!("Error starting JVM: {}", err);
            Error::JvmStartError(err)
        })?;

    let mut env = jvm.attach_current_thread().map_err(Error::CouldNotAttachToJvm)?;

    let main_class = env.find_class(config.main_class).map_err(Error::CouldNotFindMainClass)?;

    let program_args: Vec<String> = std::env::args().collect();
    let argv_strings: Vec<_> = program_args.iter().map(|it| env.new_string(it).unwrap()).collect();
    let argv: Vec<JValue> = argv_strings.iter().map(|it| JValue::from(it)).collect();

    env.call_static_method(main_class, "main", "([Ljava/lang/String;)V", &argv).unwrap();

    match env.exception_check() {
        Ok(exception) if exception => {
            let _ = env.exception_describe();
            Err(Error::ProgramError)
        },
        Ok(_) => Ok(()),
        Err(err) => Err(Error::UnexpectedJniError(err))
    }
}

fn load_config(path: &Path) -> Result<Config> {
    let file_content = fs::read_to_string(path).map_err(Error::CouldNotLoadConfig)?;
    let config: Config = toml::from_str(&file_content).map_err(Error::CouldNotReadConfig)?;
    // TODO validation?

    Ok(config)
}

#[derive(Deserialize)]
struct Config {
    jvm_args: Vec<String>,
    libjvm_path: String,
    main_class: String
}

fn build_init_args(config: &Config) -> Result<InitArgs> {
    let mut init_args_builder = InitArgsBuilder::new()
        .ignore_unrecognized(false)
        .version(JNIVersion::from(0x00150000));

    for jvm_arg in &config.jvm_args {
        init_args_builder = init_args_builder.option(jvm_arg);
    }

    init_args_builder.build().map_err(Error::CouldNotBuildInitArgs)
}