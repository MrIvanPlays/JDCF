package com.mrivanplays.jdcf.args;

/**
 * Represents a fail reason of why {@link RestArgumentAction#orElse(java.util.function.Consumer)} got invoked
 */
public enum FailReason {

    /**
     * The argument wasn't typed in the command.
     */
    ARGUMENT_NOT_TYPED,

    /**
     * The argument parsed is not the type we want it to be
     */
    ARGUMENT_PARSED_NOT_TYPE,

    /**
     * The argument parsed was null
     */
    ARGUMENT_PARSED_NULL,

    /**
     * Dummy reason for when there was no fail. This will never get invoked with a fail.
     */
    NO_FAIL_REASON
}
