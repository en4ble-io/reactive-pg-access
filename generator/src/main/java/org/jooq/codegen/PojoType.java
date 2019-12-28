package org.jooq.codegen;

/**
 * @author Mark Hofmann (mark@mark-hofmann.de)
 */
public enum PojoType {
    DTO("Dto"), FORM("Form"), VIEW("View");
    private String postfix;

    PojoType(String postfix) {
        this.postfix = postfix;
    }

    public String getPostfix() {
        return postfix;
    }
}
