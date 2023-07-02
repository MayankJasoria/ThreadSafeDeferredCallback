public record Callback(Runnable runnable, long executeAt) implements Comparable<Callback> {

    @Override
    public int compareTo(Callback o) {
        long diff = this.executeAt - o.executeAt();
        if (diff > 0) {
            return 1;
        } else if (diff == 0) {
            return 0;
        } else {
            return -1;
        }
    }
}
