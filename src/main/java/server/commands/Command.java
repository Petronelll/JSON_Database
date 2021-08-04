package server.commands;

public abstract class Command {

    protected String response;

    public String getResponse() {
        return response;
    }

    public abstract void execute();
}
