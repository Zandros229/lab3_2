import static org.junit.Assert.*;

import edu.iis.mto.staticmock.*;
import edu.iis.mto.staticmock.reader.NewsReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigurationLoader.class, NewsReaderFactory.class})
public class NewsLoaderTests {
    private NewsLoader newsLoader;
    private ConfigurationLoader configurationLoader;
    private PublishableNews publishableNews;
    private NewsReaderFactory newsReaderFactory;

    @Before
    public void setup() {
        newsLoader = new NewsLoader();
        Configuration configuration = new Configuration();
        IncomingNews incomingNews = new IncomingNews();
        incomingNews.add(new IncomingInfo("1", SubsciptionType.A));
        incomingNews.add(new IncomingInfo("2", SubsciptionType.B));
        incomingNews.add(new IncomingInfo("3", SubsciptionType.C));
        incomingNews.add(new IncomingInfo("none", SubsciptionType.NONE));

        mockStatic(ConfigurationLoader.class);
        configurationLoader = mock(ConfigurationLoader.class);
        when(ConfigurationLoader.getInstance()).thenReturn(configurationLoader);
        when(configurationLoader.loadConfiguration()).thenReturn(configuration);
        NewsReader newsReader = mock(NewsReader.class);
        when(newsReader.read()).thenReturn(incomingNews);

        mockStatic(NewsReaderFactory.class);
        newsReaderFactory = mock(NewsReaderFactory.class);
        when(NewsReaderFactory.getReader(anyString())).thenReturn(newsReader);
    }

    @Test
    public void MethodLoadConfigurationcalled() {
        publishableNews = newsLoader.loadNews();
        verify(configurationLoader, Mockito.times(1)).loadConfiguration();
    }

    @Test
    public void incommingNewsShouldBeCorrectlyDevided() {
        publishableNews = newsLoader.loadNews();
        List<String> publicMessages = Whitebox.getInternalState(publishableNews, "publicContent");
        List<String> subscribeMessages = Whitebox.getInternalState(publishableNews, "subscribentContent");

        Assert.assertEquals(1, publicMessages.size());
        Assert.assertEquals(3, subscribeMessages.size());
        Assert.assertEquals("none", publicMessages.get(0));
        Assert.assertEquals("1", subscribeMessages.get(0));
        Assert.assertEquals("2", subscribeMessages.get(1));
        Assert.assertEquals("3", subscribeMessages.get(2));
    }

}
