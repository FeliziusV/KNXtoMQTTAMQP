/*
import at.ac.tuwien.dsg.orvell.StopShellException;
import at.ac.tuwien.dsg.orvell.annotation.Command;

import java.io.InputStream;
import java.io.PrintStream;

public  class CommandShell implements Runnable {
    private at.ac.tuwien.dsg.orvell.Shell shell;


    public CommandShell(InputStream inputStream, PrintStream outputStream){
        shell=new at.ac.tuwien.dsg.orvell.Shell(inputStream,outputStream);
        shell.register(this);
        shell.setPrompt("> ");


    }
    public void run(){
        shell.run();
        shell.out().println("Exiting the shell");

    }
    @Command
    public void shutdown(){
        Main.active=false;

        throw new StopShellException();

    }

}
*/