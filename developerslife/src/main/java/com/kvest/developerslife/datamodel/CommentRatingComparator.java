package com.kvest.developerslife.datamodel;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: roman
 * Date: 1/10/14
 * Time: 7:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommentRatingComparator implements Comparator<CommentNode> {
    @Override
    public int compare(CommentNode lhs, CommentNode rhs) {
        if (lhs.rating < rhs.rating) {
            return 1;
        } else if (lhs.rating > rhs.rating) {
            return -1;
        } else {
            return 0;
        }
    }
}
