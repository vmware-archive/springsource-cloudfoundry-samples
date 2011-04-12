modules = {
    app {
        dependsOn "blueprint", "jquery"
        resource url: [dir: 'css', file: 'app.css'], attrs: [media: 'screen']
    }
}
