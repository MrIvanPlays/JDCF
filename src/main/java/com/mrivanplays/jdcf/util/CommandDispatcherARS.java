package com.mrivanplays.jdcf.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.utils.concurrent.DelayedCompletableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Deprecated
public final class CommandDispatcherARS<T> implements AuditableRestAction<T> {

    private JDA jda;
    private ScheduledExecutorService executor;

    public CommandDispatcherARS(JDA jda, ScheduledExecutorService executor) {
        this.jda = jda;
        this.executor = executor;
    }

    private void unsupported() {
        throw new UnsupportedOperationException("The specified command was executed thru dispatcher and so this isn't supported.");
    }

    @Nonnull
    @Override
    public AuditableRestAction<T> reason(@Nullable String reason) {
        unsupported();
        return null;
    }

    @Nonnull
    @Override
    public JDA getJDA() {
        return jda;
    }

    @Nonnull
    @Override
    public AuditableRestAction<T> setCheck(@Nullable BooleanSupplier checks) {
        unsupported();
        return null;
    }

    @Nonnull
    @Override
    public AuditableRestAction<T> timeout(long timeout, @Nonnull TimeUnit unit) {
        return this;
    }

    @Nonnull
    @Override
    public AuditableRestAction<T> deadline(long timestamp) {
        return this;
    }

    @Override
    public void queue() {
    }

    @Override
    public void queue(@Nullable Consumer<? super T> success) {
    }

    @Override
    public void queue(@Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure) {
    }

    @Override
    public T complete() {
        unsupported();
        return null;
    }

    @Override
    public T complete(boolean shouldQueue) throws RateLimitedException {
        unsupported();
        return null;
    }

    @Nonnull
    @Override
    public CompletableFuture<T> submit() {
        return new CompletableFuture<>();

    }

    @Nonnull
    @Override
    public CompletableFuture<T> submit(boolean shouldQueue) {
        return submit();
    }

    @Nonnull
    @Override
    public DelayedCompletableFuture<T> submitAfter(long delay, @Nonnull TimeUnit unit) {
        return submitAfter(delay, unit, executor);
    }

    @Nonnull
    @Override
    public DelayedCompletableFuture<T> submitAfter(long delay, @Nonnull TimeUnit unit, @Nullable ScheduledExecutorService executor) {
        ScheduledExecutorService usedExecutor;
        if (executor == null) {
            usedExecutor = this.executor;
        } else {
            usedExecutor = executor;
        }
        return DelayedCompletableFuture.make(usedExecutor, delay, unit, future -> () -> {
        });
    }

    @Override
    public T completeAfter(long delay, @Nonnull TimeUnit unit) {
        unsupported();
        return null;
    }

    @Nonnull
    @Override
    public ScheduledFuture<?> queueAfter(long delay, @Nonnull TimeUnit unit) {
        return queueAfter(delay, unit, t -> {
        });
    }

    @Nonnull
    @Override
    public ScheduledFuture<?> queueAfter(long delay, @Nonnull TimeUnit unit, @Nullable Consumer<? super T> success) {
        return queueAfter(delay, unit, success, Throwable::printStackTrace);
    }

    @Nonnull
    @Override
    public ScheduledFuture<?> queueAfter(long delay, @Nonnull TimeUnit unit, @Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure) {
        return executor.schedule(() -> {
            try {
                submit().thenAccept(success);
            } catch (Throwable t) {
                failure.accept(t);
            }
        }, delay, unit);
    }

    @Nonnull
    @Override
    public ScheduledFuture<?> queueAfter(long delay, @Nonnull TimeUnit unit, @Nullable ScheduledExecutorService executor) {
        return queueAfter(delay, unit, t -> {
        }, executor);
    }

    @Nonnull
    @Override
    public ScheduledFuture<?> queueAfter(long delay, @Nonnull TimeUnit unit, @Nullable Consumer<? super T> success, @Nullable ScheduledExecutorService executor) {
        return queueAfter(delay, unit, success, Throwable::printStackTrace, executor);
    }

    @Nonnull
    @Override
    public ScheduledFuture<?> queueAfter(long delay, @Nonnull TimeUnit unit, @Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure, @Nullable ScheduledExecutorService executor) {
        ScheduledExecutorService usedExecutor;
        if (executor == null) {
            usedExecutor = this.executor;
        } else {
            usedExecutor = executor;
        }
        return usedExecutor.schedule(() -> {
            try {
                submit().thenAccept(success);
            } catch (Throwable t) {
                failure.accept(t);
            }
        }, delay, unit);
    }
}
