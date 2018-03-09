package edu.wgu.dmadmin.model.publish;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

@Accessor
public interface MimeTypeAccessor {
    @Query("SELECT * FROM dm.mime_type")
    Result<MimeTypeModel> getAll();
}