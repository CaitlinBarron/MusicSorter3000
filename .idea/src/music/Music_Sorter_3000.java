package music;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.*;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsTracksRequest;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import music.Tune;

/**
 * Created by Caitlin on 11/9/2017.
 */
public class Music_Sorter_3000
{
    public  File musicFile;
    public  ArrayList<Track> playlistSongs = new ArrayList<Track>();
    public  ArrayList<String> playlistSongTitles = new ArrayList<String>();
    public  ArrayList<Tune> songList;

    public Music_Sorter_3000()
    {
        try
        {
            musicFile = new File("C:\\Users\\Caitlin\\IdeaProjects\\MusicSorter3000\\.idea\\src\\tunes");
        }
        catch(Exception ex)
        {
            System.out.println("bad file");
        }
        File[] temp = musicFile.listFiles();
        songList = new ArrayList<Tune>();
        for(File song : temp)
        {
            if(song.isFile())
            {
                Tune songItem = new Tune(song);
                songList.add(songItem);
            }
        }
        initPlaylist();
    }

    public String cleanString(String inStr)
    {
        String temp = "";
        temp = inStr.replace("\\", "");
        temp = temp.replace("/", "");
        temp = temp.replace(":", "");
        temp = temp.replace("*", "");
        temp = temp.replace("?", "");
        temp = temp.replace("\"", "");
        temp = temp.replace("<", "");
        temp = temp.replace(">", "");
        temp = temp.replace("|", "");
        return temp;
    }

    public void initPlaylist()
    {
        String accessToken = "";
        String userId = "cb987654";
        String playlistId = "5nHh2J76YNGK59wyyxJjCA";
        try
        {
            InputStream is = new FileInputStream("C:\\Users\\Caitlin\\IdeaProjects\\MusicSorter3000\\.idea\\src\\auth code.txt");
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            accessToken = buf.readLine();
            accessToken = accessToken.replace("\n", "").replace("\r", "");
            is.close();

            SpotifyApi api = new SpotifyApi.Builder().setAccessToken(accessToken).build();
            GetPlaylistsTracksRequest req = api.getPlaylistsTracks(playlistId).build();

            //get songs from playlist and fill lists with song info
            Paging<PlaylistTrack> playlist = req.execute();
            PlaylistTrack[] playlistArray = playlist.getItems();
            List<PlaylistTrack> list = Arrays.asList(playlistArray);
            for (PlaylistTrack track : list)
            {
                Track song = track.getTrack();
                playlistSongs.add(song);
                String temp = cleanString(song.getName());
                playlistSongTitles.add(temp); //used only for indexing
            }
        }
        catch (Exception ex)
        {
            System.out.println("error in initPlaylist: " + ex.getMessage());
        }
    }

    public void clean()
    {
        try
        {
            for (Tune song : songList)
            {
                String title = "";
                String artist = "";
                String album = "";

                int i = playlistSongTitles.indexOf(song.title);

                if (i != -1)
                {
                    song.spotifyFlag = true;

                    Track songB = playlistSongs.get(i);

                    title = songB.getName();

                    ArtistSimplified[] artistsArray = songB.getArtists();
                    List<ArtistSimplified> artists = Arrays.asList(artistsArray);
                    artist = artists.get(0).getName();

                    album = songB.getAlbum().getName();

                    song.saveTag(title, artist, album);

                    String tempTitle = cleanString(title);
                    String tempAlbum = cleanString(album);
                    String tempArtist = cleanString(artist);

                    song.saveCleanInfo(tempTitle, tempAlbum, tempArtist);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("error in clean: " + e.getMessage());
        }
    }

    public void sort()
    {
        String rootPath = musicFile.getPath();

        for (Tune song : songList)
        {
            if (song.spotifyFlag == true)
            {
                File artistFolder = new File(rootPath + "\\" + song.artist);
                if(!artistFolder.exists())
                {
                    artistFolder.mkdir();
                }

                File albumFolder = new File(artistFolder.getPath() + "\\" + song.album);
                if(!albumFolder.exists())
                {
                    albumFolder.mkdir();
                }

                File songB = new File(albumFolder.getPath() + "\\" + song.title + ".mp3" );
                try
                {
                    com.google.common.io.Files.move(song.songFile.getFile(), songB);
                }
                catch(Exception e)
                {
                    System.out.println("SHIT FUCK I HATE THIS. Error in sort()");
                }
            }
        }
    }

    public static void main(String[] args)
    {
        Music_Sorter_3000 sorter3000 = new Music_Sorter_3000();
        if(!sorter3000.songList.isEmpty())
        {
            sorter3000.clean();
            sorter3000.sort();
        }

    }
}
