extern crate embed_resource;

fn main() {
    embed_resource::compile("../launcher.rc", embed_resource::NONE).manifest_optional().unwrap();
    embed_resource::compile("../icon.rc", embed_resource::NONE).manifest_optional().unwrap();
}