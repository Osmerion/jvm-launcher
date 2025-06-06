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

    let config: Config = load_config(application_dir)?;
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

fn load_config(application_dir: &Path) -> Result<Config> {
    let path = application_dir.join("config.toml");
    let file_content = fs::read_to_string(path).map_err(Error::CouldNotLoadConfig)?;
    let config: Config = toml::from_str(&file_content).map_err(Error::CouldNotReadConfig)?;

    // TODO validation?

    let config: Config = Config {
        jvm_args: config.jvm_args
            .iter()
            .map(|arg| resolve_path_placeholders(arg, application_dir))
            .collect(),
        ..config
    };

    Ok(config)
}

fn resolve_path_placeholders(input: &str, application_dir: &Path) -> String {
    // Regex: match unescaped <path:...> (avoid matching \<path:...>)
    let re = regex::Regex::new(r"(\\*)<path:([^>]+)>").unwrap();

    // Replace each match with resolved absolute path
    let result = re.replace_all(input, |caps: &regex::Captures| {
        let backslashes = &caps[1];
        let relative = &caps[2];

        let num_backslashes = backslashes.len();

        if num_backslashes % 2 == 0 {
            // Not escaped: replace the whole thing with resolved path, keep half the backslashes
            let resolved = application_dir.join(relative).to_string_lossy().to_string();
            let kept_backslashes = "\\".repeat(num_backslashes / 2);
            format!("{}{}", kept_backslashes, resolved)
        } else {
            // Escaped: remove one backslash, leave the placeholder as-is
            let kept_backslashes = "\\".repeat((num_backslashes - 1) / 2);
            format!("{}<path:{}>", kept_backslashes, relative)
        }
    });

    // Remove the escape character '\' from any \<path:...> so it stays literal.
    result.replace(r"\<path:", "<path:")
}

#[derive(Deserialize)]
#[derive(Debug)]
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