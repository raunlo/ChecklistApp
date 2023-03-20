package com.raunlo.checklist.core.entity.list;

import com.raunlo.checklist.core.entity.BaseItem;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@AllArgsConstructor
@NotNull
@NoArgsConstructor
public class ItemList {
    private Collection<BaseItem> baseItems = new ArrayList<>();
    private Long id;
    private String name;
    private ListType listType;
}
