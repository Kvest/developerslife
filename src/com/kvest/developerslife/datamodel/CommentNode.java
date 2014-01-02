package com.kvest.developerslife.datamodel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 01.01.14
 * Time: 20:47
 * To change this template use File | Settings | File Templates.
 */
public class CommentNode {
    public static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);

    public long id;
    public long parentId;
    public int rating;
    public String authorName;
    public Date date;
    public String text;
    private List<CommentNode> subcomments;

    public CommentNode() {
        id = 0;
        parentId = 0;
        rating = 0;
        authorName = "";
        text = "";
        subcomments = new LinkedList<CommentNode>();
    }

    public boolean addComment(CommentNode comment) {
        if (comment.parentId == id) {
            subcomments.add(comment);

            return true;
        }

        boolean result = false;

        for (CommentNode child : subcomments) {
            if (child.addComment(comment)) {
                result = true;
                break;
            }
        }

        return result;
    }

    public String getDate() {
        return dateFormat.format(date);
    }

    public void clean() {
        id = 0;
        parentId = 0;
        rating = 0;
        authorName = "";
        text = "";
        subcomments.clear();
    }

    public void visit(Visitor visitor) {
        visit(visitor, 0);
    }

    private void visit(Visitor visitor, int level) {
        visitor.nextNode(this, level);

        for (CommentNode child : subcomments) {
            child.visit(visitor, level + 1);
        }
    }

    public interface Visitor {
        public void nextNode(CommentNode node, int level);
    }
}
