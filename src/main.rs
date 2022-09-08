extern crate jni;

use std::fs;
use std::path::Path;
use jni::objects::{JObject, JValue};
use jni::sys::JNIWrapper;
use serde::Deserialize;
use thiserror::private::PathAsDisplay;

#[derive(Deserialize)]
struct Config {
    jvm: String,
    main_class: String
}

fn main() {
    let init_args = jni::InitArgsBuilder::new()
        .ignore_unrecognized(false)
        .option("")
        .version(jni::JNIVersion::V10)
        .build()
        .unwrap();

    let app_dir = std::env::current_exe().unwrap();
    let cfg_path = app_dir.parent().unwrap().join("config.toml");

    println!("{}", cfg_path.as_display());

    let cfg_text = fs::read_to_string(cfg_path).unwrap();
    let config: Config = toml::from_str(&cfg_text).unwrap();

    println!("Foo");

    unsafe {
        let jni = JNIWrapper::new("C:/Program Files/Java/openjdk-17/bin/server/jvm.dll").unwrap();
        let jvm = jni::JavaVM::new(jni, init_args).unwrap();
        let env = jvm.attach_current_thread().unwrap();

        let class = env.find_class(config.main_class).unwrap(); // TODO find main class

        let program_args = env.new_object_array(0, env.find_class("java/lang/String").unwrap(), JObject::null()).unwrap();
        env.call_static_method(class, "main", "([Ljava/lang/String;)V", &[JValue::Object(JObject::from(program_args))]).unwrap();

        std::process::exit(if env.exception_check().unwrap() { 1 } else { 0 });
    }
}