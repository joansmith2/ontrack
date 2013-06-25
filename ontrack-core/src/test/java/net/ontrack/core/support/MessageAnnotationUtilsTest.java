package net.ontrack.core.support;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.ontrack.core.tree.Node;
import net.ontrack.core.tree.support.Markup;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MessageAnnotationUtilsTest {

    private final RegexMessageAnnotator issueMessageAnnotator = new RegexMessageAnnotator(
            "(#\\d+)",
            new Function<String, MessageAnnotation>() {
                @Override
                public MessageAnnotation apply(String match) {
                    String id = match.substring(1);
                    return MessageAnnotation.of("link").attr("url", "http://test/id/" + id).text(match);
                }
            }
    );

    private final RegexMessageAnnotator numberMessageAnnotator = new RegexMessageAnnotator(
            "(\\d+)",
            new Function<String, MessageAnnotation>() {
                @Override
                public MessageAnnotation apply(String match) {
                    return MessageAnnotation.of("emphasis").text(match);
                }
            }
    );

    @Test
    public void annotate_issue_with_one_match() {
        Node<Markup> root = MessageAnnotationUtils.annotate(
                "#177 One match",
                Arrays.asList(
                        issueMessageAnnotator
                )
        );
        // Root
        assertNotNull(root);
        assertNull(root.getData());
        assertFalse(root.isLeaf());
        // Children
        List<Node<Markup>> children = Lists.newArrayList(root.getChildren());
        assertEquals(2, children.size());
        {
            Node<Markup> link = children.get(0);
            assertEquals(
                    Markup.of("link").attr("url", "http://test/id/177"),
                    link.getData()
            );
            assertFalse(link.isLeaf());
            List<Node<Markup>> linkChildren = Lists.newArrayList(link.getChildren());
            assertEquals(1, linkChildren.size());
            {
                Node<Markup> key = linkChildren.get(0);
                assertEquals(
                        Markup.text("#177"),
                        key.getData()
                );
                assertTrue(key.isLeaf());
            }
        }
        {
            Node<Markup> child = children.get(1);
            assertEquals(
                    Markup.text(" One match"),
                    child.getData()
            );
            assertTrue(child.isLeaf());
        }
    }

    @Test
    public void annotate_issue_and_number_with_one_match() {
        Node<Markup> root = MessageAnnotationUtils.annotate(
                "#177 One match",
                Arrays.asList(
                        issueMessageAnnotator,
                        numberMessageAnnotator
                )
        );
        // Root
        assertNotNull(root);
        assertNull(root.getData());
        assertFalse(root.isLeaf());
        // Children
        List<Node<Markup>> children = Lists.newArrayList(root.getChildren());
        assertEquals(2, children.size());
        {
            Node<Markup> link = children.get(0);
            assertEquals(
                    Markup.of("link").attr("url", "http://test/id/177"),
                    link.getData()
            );
            assertFalse(link.isLeaf());
            List<Node<Markup>> linkChildren = Lists.newArrayList(link.getChildren());
            assertEquals(2, linkChildren.size());
            {
                Node<Markup> sharp = linkChildren.get(0);
                assertEquals(Markup.text("#"), sharp.getData());
            }
            {
                Node<Markup> em = linkChildren.get(1);
                assertEquals(Markup.of("emphasis"), em.getData());
                List<Node<Markup>> emChildren = Lists.newArrayList(em.getChildren());
                assertEquals(1, emChildren.size());
                {
                    Node<Markup> number = emChildren.get(0);
                    assertEquals(Markup.text("177"), number.getData());
                }
            }
        }
        {
            Node<Markup> reminder = children.get(1);
            assertEquals(
                    Markup.text(" One match"),
                    reminder.getData()
            );
            assertTrue(reminder.isLeaf());
        }
    }

}
