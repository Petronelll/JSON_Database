package server.commands;

import server.RequestHandler;

public class BadRequestCommand extends Command {

    private final RequestHandler requestHandler;

    public BadRequestCommand(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public void execute() {
        response = requestHandler.badRequest();
    }

}
