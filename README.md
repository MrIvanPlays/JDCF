![license](https://img.shields.io/github/license/MrIvanPlays/JDCF.svg?style=for-the-badge)
![issues](https://img.shields.io/github/issues/MrIvanPlays/JDCF.svg?style=for-the-badge)
[![support](https://img.shields.io/discord/493674712334073878.svg?colorB=Blue&logo=discord&label=Support&style=for-the-badge)](https://mrivanplays.com/discord)
![version](https://img.shields.io/maven-metadata/v?color=blue&label=latest%20version&metadataUrl=https%3A%2F%2Frepo.mrivanplays.com%2Frepository%2Fivan%2Fcom%2Fmrivanplays%2FJDCF%2Fmaven-metadata.xml&style=for-the-badge)

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

Using gradle:

```gradle
repositories {
    maven {
        url 'https://repo.mrivanplays.com/repository/ivan'
    }
}

dependencies {
    implementation group: 'com.mrivanplays', name: 'JDCF', version: 'VERSION' // Replace VERSION with latest version
}
```

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
// the help command.
@CommandDescription("My command runs on command")
@CommandUsage("mycommandname <something>")
@CommandAliases("commandname|mcn|cn")
public class MyCommand extends Command {

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
        args.nextString()
            .ifPresent(argument -> context.getChannel().sendMessage(argument).queue())
            .orElse(failReason -> {
                if (failReason == FailReason.ARGUMENT_NOT_TYPED) {
                    context.getChannel().sendMessage("Specify something.").queue();
                }
            });
        return true;
    }
}
```

Example command manager initialization (without overriding the default settings) and command registration
```java

public static void main(String[] args) {
    JDA jda = // assuming you have set a jda instance
    CommandManager commandManager = new CommandManager(jda);
    commandManager.registerCommand(new MyCommand());
}
```

I think you can figure out the other things yourself. For any questions you can ask me on Discord showed up.
