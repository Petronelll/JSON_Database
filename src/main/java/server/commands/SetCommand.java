package server.commands;

import server.RequestHandler;

public class SetCommand extends Command {

    private final RequestHandler requestHandler;

    public SetCommand(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public void execute() {
        response = requestHandler.set();
    }
}
