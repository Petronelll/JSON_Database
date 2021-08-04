package server.commands;

import server.RequestHandler;

public class ExitCommand extends Command {

    private final RequestHandler requestHandler;

    public ExitCommand(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public void execute() {
        response = requestHandler.exit();
    }

}
