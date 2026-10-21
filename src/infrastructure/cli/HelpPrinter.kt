package infrastructure.cli

class HelpPrinter {
    fun print() {
        println("Usage: app.jar options")
        println("Options:")
        println("    --login, -l -> User's login. (String)")
        println("    --password, -p -> User's password. (String)")
        println("    --action, -a -> User's action. (String)")
        println("    --resource, -r -> Requested resource name. (String)")
        println("    --volume, -v -> Volume of requested resource. (Int)")
        println("    --help, -h -> Usage info")
    }
}
