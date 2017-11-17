import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.PlaylistRequest;
import com.wrapper.spotify.methods.PlaylistTracksRequest;
import com.wrapper.spotify.methods.Request;
import com.wrapper.spotify.models.*;
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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Caitlin on 11/9/2017.
 */
public class Music_Sorter_3000
{
    public  File musicFile;
    //public File[] artists;
    public  ArrayList<Track> playlistSongs = new ArrayList<Track>();
    public  ArrayList<String> playlistSongTitles = new ArrayList<String>();
    public  ArrayList<AudioFile> songsToClean = new ArrayList<AudioFile>();
    public  ArrayList<AudioFile> songsToSort = new ArrayList<AudioFile>();

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
        File[] artists = musicFile.listFiles();
        for(File song : artists)
        {
            try {
                AudioFile tune = AudioFileIO.read(song);
                songsToClean.add(tune);
            }
            catch(Exception ex)
            {
                System.out.println("BAD SONG NO HOW DARE YOU"+ ex.getMessage());
            }
        }
    }

    public  void getPlaylistSongs()
    {
        String accessToken = "";
        String userId = "cb987654";
        String playlistId = "5nHh2J76YNGK59wyyxJjCA";

        try {
            InputStream is = new FileInputStream("C:\\Users\\Caitlin\\IdeaProjects\\MusicSorter3000\\.idea\\src\\auth code.txt");
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            accessToken = buf.readLine();
            accessToken = accessToken.replace("\n", "").replace("\r", "");
            is.close();

            Api api = Api.builder().accessToken(accessToken).build();
            PlaylistRequest req = api.getPlaylist(userId, playlistId).build();

            Playlist playlist = req.get();
            List<PlaylistTrack> list = playlist.getTracks().getItems();
            for (PlaylistTrack track : list)
            {
                Track song = track.getTrack();
                playlistSongs.add(song);
                playlistSongTitles.add(song.getName());
            }
        }
        catch (Exception e)
        {
            System.out.println("Something went wrong!" + e.getMessage());
        }

    }

    public  void clean()
    {
        try {
            String title = "";
            String artist = "";
            String album = "";

            for (AudioFile songA : songsToClean) {
                Tag tag = songA.getTag();
                String songATitle = tag.getFirst(FieldKey.TITLE);
                int i = playlistSongTitles.indexOf(songATitle);

                if (i != -1) {
                    Track songB = playlistSongs.get(i);

                    title = songB.getName();

                    List<SimpleArtist> artists = songB.getArtists();
                    artist = artists.get(0).getName();

                    album = songB.getAlbum().getName();

                } else {
                    System.out.println("SHIT");
                }

                tag.setField(FieldKey.ALBUM, album);
                tag.setField(FieldKey.ARTIST, artist);
                tag.setField(FieldKey.COMMENT, "");
                songA.commit();

                String tempTitle = tag.getFirst(FieldKey.TITLE);
                String path = musicFile.getPath();

                File temp = new File(path + "\\" + tempTitle + ".mp3");
                songA.getFile().renameTo(temp);
                songsToSort.add(songA);
            }
        }
        catch (Exception e)
        {
            System.out.println("SHIT FUCK"+ e.getMessage());
        }
    }

    public  void sort()
    {
        String rootPath = musicFile.getPath();

        for (AudioFile song : songsToSort)
        {
            Tag tag = song.getTag();
            String artist = tag.getFirst(FieldKey.ARTIST);
            String album = tag.getFirst(FieldKey.ALBUM);

            File artistFolder = new File(rootPath + "\\" + artist);
            if(!artistFolder.exists())
            {
                artistFolder.mkdir();
            }

            File albumFolder = new File(artistFolder.getPath() + "\\" + album);
            if(!albumFolder.exists())
            {
                albumFolder.mkdir();
            }

            String title = tag.getFirst(FieldKey.TITLE);
            File songA = song.getFile();
            File songB = new File(albumFolder.getPath() + "\\" + title + ".mp3" );
            //if(songA.renameTo(songB))
            //{
            //    System.out.println("success");
            //}
            try
            {
                com.google.common.io.Files.move(songA, songB);
            }
            catch(Exception e)
            {
                System.out.println("SHIT FUCK I HATE THIS "+ e.getMessage());
            }

        }
    }

    public static void main(String[] args)
    {
        Music_Sorter_3000 sorter3000 = new Music_Sorter_3000();
        sorter3000.getPlaylistSongs();
        if(!sorter3000.songsToClean.isEmpty())
        {
            sorter3000.clean();
        }

        if(!sorter3000.songsToSort.isEmpty())
        {
            sorter3000.sort();
        }

    }
}
