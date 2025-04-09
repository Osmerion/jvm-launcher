use thiserror::Error;
use thistermination::Termination;

#[derive(Error, Termination)]
pub enum Error {
    #[termination(exit_code(10))]
    #[error(transparent)]
    UnknownExecutable(std::io::Error),

    #[termination(exit_code(10))]
    #[error("Unknown application directory")]
    UnknownApplicationDir,

    #[termination(exit_code(11))]
    #[error("Could not load configuration file")]
    CouldNotLoadConfig(std::io::Error),

    #[termination(exit_code(12))]
    #[error("Unknown application directory")]
    CouldNotSetCurrentDir(std::io::Error),


    #[termination(exit_code(20))]
    #[error("Could not parse configuration file")]
    CouldNotReadConfig(toml::de::Error),


    #[termination(exit_code(30))]
    #[error("Could not start JVM")]
    JvmStartError(jni::errors::StartJvmError),

    #[termination(exit_code(31))]
    #[error("Could not find main class")]
    CouldNotFindMainClass(jni::errors::Error),

    #[termination(exit_code(32))]
    #[error("Could not attach thread to JVM")]
    CouldNotAttachToJvm(jni::errors::Error),

    #[termination(exit_code(39))]
    #[error("Unexpected JNI error")]
    UnexpectedJniError(jni::errors::Error),

    #[termination(exit_code(40))]
    #[error("Could not build JVM initialization arguments")]
    CouldNotBuildInitArgs(jni::JvmError),

    #[termination(exit_code(50))]
    #[error("Uncaught exception from Java code")]
    ProgramError
}