/*
 * Copyright (c) 2016 HERE All rights reserved.
 */

package org.wa9nnn.wfdserver.actor;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER })
@BindingAnnotation
public @interface BulkLoaderActorAnno
{
}
