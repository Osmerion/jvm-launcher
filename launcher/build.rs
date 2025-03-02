extern crate embed_resource;

fn main() {
    let descriptor_path = std::env::var("OSMERION_jvmLauncher_descriptor").unwrap();
    embed_resource::compile(descriptor_path, embed_resource::NONE).manifest_optional().unwrap();

    if let Ok(icon_path) = std::env::var("OSMERION_jvmLauncher_icon") {
        embed_resource::compile(icon_path, embed_resource::NONE).manifest_optional().unwrap();
    }
}