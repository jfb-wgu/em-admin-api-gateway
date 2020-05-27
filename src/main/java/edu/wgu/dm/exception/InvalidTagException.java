package edu.wgu.dm.exception;

import edu.wgu.dm.tag.Tag;

public class InvalidTagException extends RuntimeException {

    public InvalidTagException(Tag tag) {
        super("Invalid Tag. 1.Tag name must be unique. 2.Character limit for name is 60 and for description its 255. 3. Role id must be valid. Please check "
                  + tag);
    }
}
