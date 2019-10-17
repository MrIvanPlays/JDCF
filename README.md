![license](https://img.shields.io/github/license/MrIvanPlays/JDCF.svg?style=for-the-badge)
![issues](https://img.shields.io/github/issues/MrIvanPlays/JDCF.svg?style=for-the-badge)
[![support](https://img.shields.io/discord/493674712334073878.svg?colorB=Blue&logo=discord&label=Support&style=for-the-badge)](https://mrivanplays.com/discord)

# JDCF

A command framework for the famous discord api for java - [JDA](https://github.com/DV8FromTheWorld/JDA)

## Installation

Using maven:

```xml
<repositories>
  <repository>
    <id>ivan</id>
    <url>https://repo.mrivanplays.com/repository/ivan/</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.mrivanplays</groupId>
    <artifactId>JDCF</artifactId>
    <version>VERSION</version> <!-- Replace VERSION with latest version -->
    <scope>compile</scope>  
  </dependency>
</dependencies>
```

I don't use gradle that much so I can't show how to do it with gradle.

## Usage

Example command:

```java
import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.args.FailReason;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;

// the usage and description aren't necessary if you don't enable
// the help command. the aliases are also not required to have
@CommandDescription("My command runs on command")
@CommandUsage("mycommandname <something>")
@CommandAliases("commandname|mcn|cn")
public class MyCommand extends Command
{

    public MyCommand()
    {
        super("mycommandname", Permission.MANAGE_SERVER, Permission.MANAGE_PERMISSIONS); // the permissions are not necessary 
    }

    @Override
    public void execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        args.nextString().ifPresent(argument -> context.getChannel().sendMessage(argument).queue()).orElse(failReason -> {
            if (failReason == FailReason.ARGUMENT_NOT_TYPED)
            {
                context.getChannel().sendMessage("Specify something.").queue();
            }
        });
    }
}
```

Example command manager initialization (without overriding the default settings) and command registration
```java

public static void main(String[] args)
{
    JDA jda = // assuming you have set a jda instance
    CommandManager commandManager = new CommandManager(jda);
    commandManager.registerCommand(new MyCommand());
}
```

I think you can figure out the other things yourself. For any questions you can ask me on Discord showed up.