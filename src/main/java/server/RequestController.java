package server;

import server.commands.Command;

public class RequestController {

    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void executeCommand() {
        command.execute();
    }

    public String getResponse() {
        return command.getResponse();
    }
}
