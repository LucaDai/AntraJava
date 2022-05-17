import java.util.*;
import org.junit.*;


import static java.util.Optional.empty;
import static org.hamcrest.MatcherAssert.assertThat;

class Test1 {
    @Test
    public void cacheIsWorking() {
        SongCache cache = new SongCacheImpl();
        cache.recordSongPlays("ID-1", 3);
        cache.recordSongPlays("ID-1", 1);
        cache.recordSongPlays("ID-2", 2);
        cache.recordSongPlays("ID-3", 5);
        assertThat(cache.getPlaysForSong("ID-1"), is(4));
        assertThat(cache.getPlaysForSong("ID-9"), is(-1));
        assertThat(cache.getTopNSongsPlayed(2), contains("ID-3",
                "ID-1"));
        assertThat(cache.getTopNSongsPlayed(0), is(empty()));
    }

}
interface SongCache {
    /**
     * Record number of plays for a song.
     */
    void recordSongPlays(String songId, int numPlays);
    /**
     * Fetch the number of plays for a song.
     *
     * @return the number of plays, or -1 if the
    song ID is unknown.
     */
    int getPlaysForSong(String songId);
    /**
     * Return the top N songs played, in descending
     order of number of plays.
     */
    List<String> getTopNSongsPlayed(int n);
}

class SongCacheImpl implements SongCache {
    HashMap<String, Integer> record = new HashMap<>();
    @Override
    synchronized public void recordSongPlays(String songId, int numPlays) {
        record.put(songId, record.getOrDefault(songId, 0) + numPlays);
    }
    @Override
    synchronized public int getPlaysForSong(String songId) {
        return record.getOrDefault(songId, -1);
    }

    @Override
    synchronized public List<String> getTopNSongsPlayed(int n) {
        List<String> song = new ArrayList<>();
        List<String>[] bucket = new List[record.size() + 1];
        for (String songId : record.keySet()) {
            int numPlays = record.get(songId);
            if (bucket[numPlays] == null) {
                bucket[numPlays] = new ArrayList();
            }
            bucket[numPlays].add(songId);
        }

        for (int i = 0; i <= bucket.length; i++) {
            if(bucket[i] == null) continue;
            while (!bucket[i].isEmpty() && --n >= 0) {
                song.add(bucket[i].remove(0));
                if (n == 0) break;
            }
        }
        return song;
    }

}
