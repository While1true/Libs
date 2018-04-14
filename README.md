# RxLibs_
Rxjava retrofit okhttp
public class RxManager {

    public static <T> T create(Class<T> service) {
        return SingleHolder.manger.get().create(service);
    }

    public static RetrofitHttpManger get() {
        return SingleHolder.manger;
    }

    private static class SingleHolder {
        //TODO new RetrofitHttpManger.Builder()配置你的基础参数
        private static RetrofitHttpManger manger = new RetrofitHttpManger.Builder().Builder();
    }
}
