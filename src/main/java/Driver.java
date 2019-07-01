import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class Driver {
    public static void main(String[] args) {
        Mono<AString>  a = getString();
        Mono<Bstring> b = getBString();
        String result = Flux.zip(a, b)
                .checkpoint()
                .doOnError(t -> {
                    throw new RuntimeException();
                })
                .map(tuple -> {
                    System.out.println("Main Current thread is: " + Thread.currentThread().getName());
                    return tuple.getT1().getExtra() + tuple.getT2().getExtra();
                }).blockFirst();
        System.out.println(result);
    }

    protected static Mono<AString> getString(){
            return Mono.just("abc")
                .subscribeOn(Schedulers.parallel())
                .map(data -> {

                    System.out.println("Current thread is: " + Thread.currentThread().getName());
                    if(1 ==1 ) {
                        throw new RuntimeException();
                    }
                    return new AString(1);
                })
                    .doOnCancel(() -> {
                        System.out.println("getAString canceled");
                    });
    }

    protected static Mono<Bstring> getBString(){
        return Mono.just("abc")
                .subscribeOn(Schedulers.parallel())
                .map(data -> {
                    System.out.println("Current thread is: " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return new Bstring("bc");
                }).doOnCancel(() -> {
                    System.out.println("getBString canceled");
                });
    }
}
