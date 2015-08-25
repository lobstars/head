package com.lobstar.head.module;

import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.ioc.provider.AnnotationIocProvider;

@Modules(scanPackage=true)
@IocBy(type=AnnotationIocProvider.class,args="com.lobstar.head")
public class MainModule {

}
