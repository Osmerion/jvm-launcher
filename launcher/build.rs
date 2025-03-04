extern crate embed_resource;

fn main() {
    if let Ok(versioninfo_path) = std::env::var("OSMERION_jvmLauncher_versioninfo") {
        embed_resource::compile(versioninfo_path, embed_resource::NONE).manifest_optional().unwrap();
    }

    if let Ok(icon_path) = std::env::var("OSMERION_jvmLauncher_icon") {
        embed_resource::compile(icon_path, embed_resource::NONE).manifest_optional().unwrap();
    }
}