package com.order.manager.enums;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;

public enum OrderLineStatus implements Serializable {
    DRAFT,
    DELETED;
}
