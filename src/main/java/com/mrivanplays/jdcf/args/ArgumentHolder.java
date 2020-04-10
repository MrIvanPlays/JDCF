package com.mrivanplays.jdcf.args;

import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.MayBeEmpty;
import com.mrivanplays.jdcf.args.chain.ArgumentChain;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

// work in progress
public class ArgumentHolder {

    public static <T, D> Optional<T> parseArgument(@NotNull ArgumentResolver<T, D> resolver,
                                                   @Nullable String rawArgument,
                                                   @NotNull JDA jda,
                                                   @Nullable Guild guild,
                                                   int argumentPosition) {
        Objects.requireNonNull(resolver, "resolver");
        Objects.requireNonNull(jda, "jda");
        String argument = rawArgument == null ? "" : rawArgument;
        return resolver.parse(new ArgumentResolveContext(argument, jda, guild, argumentPosition)).getValue();
    }

    private List<String> args;
    private final CommandExecutionContext commandContext;
    private final ArgumentResolveGlobalFailHandler globalFailHandler;
    private int argumentsTookCount = 0;

    public ArgumentHolder(@NotNull CommandExecutionContext commandContext, @Nullable String[] args) {
        this.args = args == null ? Collections.emptyList() : new ArrayList<>(Arrays.asList(args));
        this.commandContext = Objects.requireNonNull(commandContext, "commandContext");
        this.globalFailHandler = commandContext.getCommandManagerCreator().getSettings().getGlobalFailHandler();
    }

    @NotNull
    public ArgumentChain newArgumentChain() {
        return new ArgumentChain(this);
    }

    @NotNull
    public <T, D> Argument<T, D> next(@NotNull ArgumentResolver<T, D> argumentResolver) {
        Objects.requireNonNull(argumentResolver, "argumentResolver");
        argumentsTookCount++;
        try {
            String argument = args.remove(0);
            return new Argument<>(
                    commandContext,
                    argumentResolver,
                    argument,
                    globalFailHandler,
                    argumentsTookCount - 1
            );
        } catch (IndexOutOfBoundsException e) { // I hate lists and java's exceptions
            return new Argument<>(
                    commandContext,
                    argumentResolver,
                    null,
                    globalFailHandler,
                    argumentsTookCount - 1
            );
        }
    }

    @NotNull
    public Argument<String, Object> nextString() {
        return next(ArgumentResolvers.STRING);
    }

    @NotNull
    public Argument<Integer, Object> nextInt() {
        return next(ArgumentResolvers.INTEGER);
    }

    @NotNull
    public Argument<Double, Object> nextDouble() {
        return next(ArgumentResolvers.DOUBLE);
    }

    @NotNull
    public Argument<Float, Object> nextFloat() {
        return next(ArgumentResolvers.FLOAT);
    }

    @NotNull
    public Argument<Long, Object> nextLong() {
        return next(ArgumentResolvers.LONG);
    }

    public Optional<String> get(int argumentNumber) {
        try {
            return Optional.of(args.get(argumentNumber));
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    @NotNull
    public <T, D> Argument<T, D> getArgument(@NotNull ArgumentResolver<T, D> resolver, int argumentNumber) {
        Objects.requireNonNull(resolver, "resolver");
        try {
            String argument = args.get(argumentNumber);
            return new Argument<>(
                    commandContext,
                    resolver,
                    argument,
                    globalFailHandler,
                    argumentNumber
            );
        } catch (IndexOutOfBoundsException e) {
            return new Argument<>(
                    commandContext,
                    resolver,
                    null,
                    globalFailHandler,
                    argumentNumber
            );
        }
    }

    public <T, D> Optional<T> parseNext(@NotNull ArgumentResolver<T, D> resolver) {
        Objects.requireNonNull(resolver, "resolver");
        argumentsTookCount++;
        try {
            String argument = args.remove(0);
            return ArgumentHolder.parseArgument(resolver, argument,
                    commandContext.getJda(), commandContext.getGuild(),
                    argumentsTookCount - 1);
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    public <T, D> Optional<T> parse(@NotNull ArgumentResolver<T, D> resolver, int argumentNumber) {
        Objects.requireNonNull(resolver, "resolver");
        try {
            String argument = args.get(argumentNumber);
            return ArgumentHolder.parseArgument(resolver, argument, commandContext.getJda(), commandContext.getGuild(),
                    argumentNumber);
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    public String join(int argumentFrom, @NotNull CharSequence separator) {
        Objects.requireNonNull(separator, "separator");
        StringBuilder builder = new StringBuilder();
        for (int i = argumentFrom; i < args.size(); i++) {
            builder.append(i).append(separator);
        }
        return builder.substring(0, builder.length() - separator.length());
    }

    public int size() {
        return args.size();
    }

    @NotNull
    @MayBeEmpty
    public String[] getRawArgumentsArray() {
        return args.toArray(new String[0]);
    }

    @NotNull
    @MayBeEmpty
    public List<String> getRawArgumentsList() {
        return Collections.unmodifiableList(args);
    }
}
