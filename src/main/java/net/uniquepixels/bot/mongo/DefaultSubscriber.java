package net.uniquepixels.bot.mongo;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Optional;
import java.util.function.Consumer;

public record DefaultSubscriber<T>(Consumer<Optional<T>> consumer, long request) implements Subscriber<T> {

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(this.request);
    }

    @Override
    public void onNext(T t) {
        consumer.accept(Optional.of(t));
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.fillInStackTrace();
        consumer.accept(Optional.empty());
    }

    @Override
    public void onComplete() {

    }
}
