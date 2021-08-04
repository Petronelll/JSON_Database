package server.commands;

import server.RequestHandler;

public class DeleteCommand extends Command {
    private final RequestHandler requestHandler;

    public DeleteCommand(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public void execute() {
        response = requestHandler.delete();
    }
}
