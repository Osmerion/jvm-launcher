extern crate embed_resource;

fn main() {
    embed_resource::compile("launcher.rc");
    embed_resource::compile("icon.rc");
}