class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(controller: "article", action: "index")
        "500"(view:'/error')
    }
}
