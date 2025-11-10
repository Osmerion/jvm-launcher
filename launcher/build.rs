extern crate embed_resource;

fn main() {
    if let Ok(extra_resources_paths) = std::env::var("OSMERION_jvmLauncher_resources") {
        for path in extra_resources_paths.split(';') {
            let path = path.trim();
            if !path.is_empty() {
                embed_resource::compile(path, embed_resource::NONE).manifest_optional().unwrap();
            }
        }
    }
}