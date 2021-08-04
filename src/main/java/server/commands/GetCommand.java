package server.commands;

import server.RequestHandler;

public class GetCommand extends Command {

    private final RequestHandler requestHandler;

    public GetCommand(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public void execute() {
        response = requestHandler.get();
    }
}
