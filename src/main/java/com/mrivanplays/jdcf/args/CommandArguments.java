package com.mrivanplays.jdcf.args;

import com.mrivanplays.jdcf.CommandExecutionContext;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.CheckReturnValue;

/**
 * Represents a class, containing the actual argument resolving of a command.
 */
public final class CommandArguments {

    private final List<String> args;
    private final CommandExecutionContext commandContext;
    private FailReasonHandler failReasonHandler;

    public CommandArguments(CommandExecutionContext commandContext, String[] args) {
        this(commandContext, new ArrayList<>(Arrays.asList(args)));
    }

    public CommandArguments(CommandExecutionContext commandContext, List<String> args) {
        this.args = args;
        this.commandContext = commandContext;
        this.failReasonHandler = commandContext.getCommandManagerCreator().getSettings().getFailReasonHandler();
    }

    /**
     * Gets the next argument while decrementing the {@link #size()}. This method is unsafe as the method may return
     * null if there are no arguments and the safe alternatives of this method are {@link #next()} or {@link
     * #next(ArgumentResolver)}. Be careful when using the methods with
     * <code>next
     * </code> in their name!
     *
     * @return a string argument or null
     */
    @Nullable
    public String nextUnsafe() {
        return getArgUnsafe(0);
    }

    /**
     * Gets the next argument while decrementing the {@link #size()}. Be careful when using the methods with
     * <code>next</code> in their name!
     *
     * @return a optional with string argument if present, a empty optional if not present
     */
    public Optional<String> next() {
        return Optional.ofNullable(nextUnsafe());
    }

    /**
     * Gets the specified argument while decrementing the {@link #size()}. This method is unsafe as the method may
     * return null if there are no arguments and the safe alternative of this method is {@link #getArg(int)}. Be very
     * careful how you use this.
     *
     * @param arg the argument you want to get
     * @return the string argument or null
     */
    @Nullable
    public String getArgUnsafe(int arg) {
        if (args.size() == 0) {
            return null;
        }
        return args.remove(arg);
    }

    /**
     * Gets the specified argument while decrementing the {@link #size()}. Be very careful how you use this.
     *
     * @param arg the argument yiu want to get
     * @return a optional with string argument if present, a empty optional if not present
     */
    public Optional<String> getArg(int arg) {
        return Optional.ofNullable(getArgUnsafe(arg));
    }

    /**
     * Resolves the next argument to the specified resolver. The specified method decrements {@link #size()} and if you
     * run that method like that:
     *
     * <pre><code>
     * public void execute(CommandExecutionContext context, CommandArguments args) {
     *     args.next(ArgumentResolvers.INTEGER).ifPresent(number -&#62; {
     *         // handling
     *     }).orElse(failReason -&#62; {
     *         // handling
     *     });
     *     args.next(ArgumentResolvers.INTEGER).ifPresent(number -&#62; {
     *         // handling
     *     }).orElse(failReason -&#62; {
     *         // handling
     *     });
     * }</code></pre>
     *
     * <p>The 2nd get will get the next argument after the 1st call so it won't be equal to the first
     * one. That's why 2nd or more arguments should be in the call before to have access to all of the arguments you
     * need. Be careful when using the methods with <code>next</code> in their name!
     *
     * @param resolver the resolver of the argument you want to resolve.
     * @param <T>      the type of the argument
     * @return empty {@link ArgumentOptional} if argument not parsed, or the argument parsed is not the type, or the
     * type parsed is null.
     */
    @NotNull
    @CheckReturnValue
    public <T> ArgumentOptional<T> next(@NotNull ArgumentResolver<T> resolver) {
        Objects.requireNonNull(resolver, "resolver");
        if (args.size() == 0) {
            return ArgumentOptional.of(null, FailReason.ARGUMENT_NOT_TYPED, null, commandContext, failReasonHandler);
        }
        String argument = nextUnsafe();
        if (argument == null) {
            return ArgumentOptional.of(null, FailReason.ARGUMENT_NOT_TYPED, null, commandContext, failReasonHandler);
        }
        try {
            T resolved = resolver.resolve(new ArgumentResolverContext(argument, commandContext.getGuild(), commandContext.getJda()));
            if (resolved == null) {
                return ArgumentOptional.of(null, FailReason.ARGUMENT_PARSED_NULL, argument, commandContext, failReasonHandler);
            }
            return ArgumentOptional.of(resolved, FailReason.NO_FAIL_REASON, argument, commandContext, failReasonHandler);
        } catch (Throwable error) {
            return ArgumentOptional.of(null, FailReason.ARGUMENT_PARSED_NOT_TYPE, argument, commandContext, failReasonHandler);
        }
    }

    @NotNull
    @CheckReturnValue
    public ArgumentOptional<Integer> nextInt() {
        return next(ArgumentResolvers.INTEGER);
    }

    @NotNull
    @CheckReturnValue
    public ArgumentOptional<String> nextString() {
        if (args.size() == 0) {
            return ArgumentOptional.of(null, FailReason.ARGUMENT_NOT_TYPED, null, commandContext, failReasonHandler);
        }
        String argument = nextUnsafe();
        if (argument == null) {
            return ArgumentOptional.of(null, FailReason.ARGUMENT_NOT_TYPED, null, commandContext, failReasonHandler);
        }
        return ArgumentOptional.of(argument, FailReason.NO_FAIL_REASON, argument, commandContext, failReasonHandler);
    }

    @NotNull
    @CheckReturnValue
    public ArgumentOptional<Double> nextDouble() {
        return next(ArgumentResolvers.DOUBLE);
    }

    @NotNull
    @CheckReturnValue
    public ArgumentOptional<Float> nextFloat() {
        return next(ArgumentResolvers.FLOAT);
    }

    /**
     * Joins the specified arguments with space as a delimiter.
     *
     * @param from from which argument the joiner should start
     * @return joined string from arguments with space as delimiter
     */
    public String joinArgumentsSpace(int from) {
        return joinArguments(from, ' ');
    }

    /**
     * Joins the specified arguments with the character specified.
     *
     * @param from      from which argument the joiner should start
     * @param separator the separator used to join the arguments
     * @return joined string from arguments with the separator as delimiter
     */
    public String joinArguments(int from, char separator) {
        StringBuilder builder = new StringBuilder();
        for (int i = from; i < args.size(); i++) {
            builder.append(args.get(i)).append(separator);
        }
        return builder.toString().trim();
    }

    /**
     * Joins the specified arguments with the specified characters.
     *
     * @param from      from which argument the joiner should start
     * @param separator the characters used to join the arguments
     * @return joined string from arguments with the characters separator as delimiter
     */
    public String joinArguments(int from, CharSequence separator) {
        StringBuilder builder = new StringBuilder();
        for (int i = from; i < args.size(); i++) {
            builder.append(args.get(i)).append(separator);
        }
        return builder.toString().trim();
    }

    /**
     * Returns the count of the specified arguments. This count will decrement whenever a argument was got from any of
     * the methods except {@link #joinArguments(int, char)}
     *
     * @return specified arguments count
     */
    public int size() {
        return args.size();
    }

    /**
     * Returns the raw arguments array. If any method in this class containing "next" in his name is being called before
     * this method, this method won't return all of the arguments.
     *
     * <p>It is generally preferable to not use this method
     *
     * @return raw arguments left or null if none
     */
    @Nullable
    public String[] getArgsLeft() {
        if (args.isEmpty()) {
            return null;
        }
        return args.toArray(new String[0]);
    }

    /**
     * Returns the raw list of arguments left. If any method in this class contains "next" in his name is being called
     * before this method, the list won't have all the arguments.
     *
     * <p>It is generally preferable to not use this method
     *
     * @return list of arguments left or empty list if none
     */
    @NotNull
    public List<String> getArgsLeftList() {
        return args;
    }

    /**
     * Creates a new copy of this command arguments.
     *
     * @return instance copy
     */
    @NotNull
    public CommandArguments copy() {
        return new CommandArguments(commandContext, args);
    }
}
