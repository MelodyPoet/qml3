package util;

public @interface NamedArg {
    /**
     * The name of the annotated argument.
     * @return the name of the annotated argument
     */
    public String value();

    /**
     * The default value of the annotated argument.
     * @return the default value of the annotated argument
     */
    public String defaultValue() default "";
}
