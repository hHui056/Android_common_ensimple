package com.allen.common.utils

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.util.*

/**
 * Create By hHui on 2017/09/15.
 *
 * 反射工具类，提供一些Java基本的反射功能
 */
object ReflectUtils {
    val EMPTY_PARAMS = arrayOfNulls<Any>(0)

    /* ************************************************** 字段相关的方法 ******************************************************* */
    /**
     * 从指定的类中获取指定的字段

     * @param sourceClass         指定的类
     * *
     * @param fieldName           要获取的字段的名字
     * *
     * @param isFindDeclaredField 是否查找Declared字段
     * *
     * @param isUpwardFind        是否向上去其父类中寻找
     * *
     * @return
     */
    @JvmOverloads fun getField(sourceClass: Class<*>, fieldName: String, isFindDeclaredField: Boolean = true, isUpwardFind: Boolean = true): Field? {
        var field: Field? = null
        try {
            field = if (isFindDeclaredField) sourceClass.getDeclaredField(fieldName) else sourceClass.getField(fieldName)
        } catch (e1: NoSuchFieldException) {
            if (isUpwardFind) {
                var classs: Class<*>? = sourceClass.superclass
                while (field == null && classs != null) {
                    try {
                        field = if (isFindDeclaredField) classs.getDeclaredField(fieldName) else classs.getField(fieldName)
                    } catch (e11: NoSuchFieldException) {
                        classs = classs.superclass
                    }

                }
            }
        }

        return field
    }

    /**
     * 获取给定类的所有字段

     * @param sourceClass         给定的类
     * *
     * @param isGetDeclaredField  是否需要获取Declared字段
     * *
     * @param isGetParentField    是否需要把其父类中的字段也取出
     * *
     * @param isGetAllParentField 是否需要把所有父类中的字段全取出
     * *
     * @param isDESCGet           在最终获取的列表里，父类的字段是否需要排在子类的前面。只有需要把其父类中的字段也取出时此参数才有效
     * *
     * @return 给定类的所有字段
     */
    @JvmOverloads fun getFields(sourceClass: Class<*>, isGetDeclaredField: Boolean = true, isGetParentField: Boolean = true, isGetAllParentField: Boolean = true, isDESCGet: Boolean = true): List<Field> {
        val fieldList = ArrayList<Field>()
        //如果需要从父类中获取
        if (isGetParentField) {
            //获取当前类的所有父类
            var classList: MutableList<Class<*>>? = null
            if (isGetAllParentField) {
                classList = getSuperClasss(sourceClass, true)
            } else {
                classList = ArrayList<Class<*>>(2)
                classList.add(sourceClass)
                val superClass = sourceClass.superclass
                if (superClass != null) {
                    classList.add(superClass)
                }
            }

            //如果是降序获取
            if (isDESCGet) {
                for (w in classList.size - 1 downTo -1 + 1) {
                    for (field in if (isGetDeclaredField) classList[w].declaredFields else classList[w].fields) {
                        fieldList.add(field)
                    }
                }
            } else {
                for (w in classList.indices) {
                    for (field in if (isGetDeclaredField) classList[w].declaredFields else classList[w].fields) {
                        fieldList.add(field)
                    }
                }
            }
        } else {
            for (field in if (isGetDeclaredField) sourceClass.declaredFields else sourceClass.fields) {
                fieldList.add(field)
            }
        }
        return fieldList
    }

    /**
     * 设置给定的对象中给定名称的字段的值

     * @param object              给定的对象
     * *
     * @param fieldName           要设置的字段的名称
     * *
     * @param newValue            要设置的字段的值
     * *
     * @param isFindDeclaredField 是否查找Declared字段
     * *
     * @param isUpwardFind        如果在当前类中找不到的话，是否取其父类中查找
     * *
     * @return 设置是否成功。false：字段不存在或新的值与字段的类型不一样，导致转型失败
     */
    fun setField(obj: Any, fieldName: String, newValue: Any, isFindDeclaredField: Boolean, isUpwardFind: Boolean): Boolean {
        var result = false
        val field = getField(obj.javaClass, fieldName, isFindDeclaredField, isUpwardFind)
        if (field != null) {
            try {
                field.isAccessible = true
                field.set(obj, newValue)
                result = true
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
                result = false
            }

        }
        return result
    }


    /* ************************************************** 方法相关的方法 ******************************************************* */
    /**
     * 从指定的类中获取指定的方法

     * @param sourceClass          给定的类
     * *
     * @param isFindDeclaredMethod 是否查找Declared字段
     * *
     * @param isUpwardFind         是否向上去其父类中寻找
     * *
     * @param methodName           要获取的方法的名字
     * *
     * @param methodParameterTypes 方法参数类型
     * *
     * @return 给定的类中给定名称以及给定参数类型的方法
     */
    fun getMethod(sourceClass: Class<*>, isFindDeclaredMethod: Boolean, isUpwardFind: Boolean, methodName: String, vararg methodParameterTypes: Class<*>): Method? {
        var method: Method? = null
        try {
            method = if (isFindDeclaredMethod) sourceClass.getDeclaredMethod(methodName, *methodParameterTypes) else sourceClass.getMethod(methodName, *methodParameterTypes)
        } catch (e1: NoSuchMethodException) {
            if (isUpwardFind) {
                var classs: Class<*>? = sourceClass.superclass
                while (method == null && classs != null) {
                    try {
                        method = if (isFindDeclaredMethod) classs.getDeclaredMethod(methodName, *methodParameterTypes) else classs.getMethod(methodName, *methodParameterTypes)
                    } catch (e11: NoSuchMethodException) {
                        classs = classs.superclass
                    }

                }
            }
        }

        return method
    }

    /**
     * 从指定的类中获取指定的方法，默认获取Declared类型的方法、向上查找

     * @param sourceClass          指定的类
     * *
     * @param methodName           方法名
     * *
     * @param methodParameterTypes 方法参数类型
     * *
     * @return
     */
    @JvmOverloads fun getMethod(sourceClass: Class<*>, methodName: String, vararg methodParameterTypes: Class<*>): Method? {
        return getMethod(sourceClass, true, true, methodName, *methodParameterTypes)
    }

    /**
     * 获取给定类的所有方法

     * @param clas                给定的类
     * *
     * @param isGetDeclaredMethod 是否需要获取Declared方法
     * *
     * @param isFromSuperClassGet 是否需要把其父类中的方法也取出
     * *
     * @param isDESCGet           在最终获取的列表里，父类的方法是否需要排在子类的前面。只有需要把其父类中的方法也取出时此参数才有效
     * *
     * @return 给定类的所有方法
     */
    fun getMethods(clas: Class<*>, isGetDeclaredMethod: Boolean, isFromSuperClassGet: Boolean, isDESCGet: Boolean): List<Method> {
        val methodList = ArrayList<Method>()
        //如果需要从父类中获取
        if (isFromSuperClassGet) {
            //获取当前类的所有父类
            val classList = getSuperClasss(clas, true)
            //如果是降序获取
            if (isDESCGet) {
                for (w in classList.size - 1 downTo -1 + 1) {
                    for (method in if (isGetDeclaredMethod) classList[w].declaredMethods else classList[w].methods) {
                        methodList.add(method)
                    }
                }
            } else {
                for (w in classList.indices) {
                    for (method in if (isGetDeclaredMethod) classList[w].declaredMethods else classList[w].methods) {
                        methodList.add(method)
                    }
                }
            }
        } else {
            for (method in if (isGetDeclaredMethod) clas.declaredMethods else clas.methods) {
                methodList.add(method)
            }
        }
        return methodList
    }

    /**
     * 获取给定类的所有方法

     * @param sourceClass 给定的类
     * *
     * @return 给定类的所有方法
     */
    fun getMethods(sourceClass: Class<*>): List<Method> {
        return getMethods(sourceClass, true, true, true)
    }

    /**
     * 获取给定的类中指定参数类型的ValuOf方法

     * @param sourceClass          给定的类
     * *
     * @param methodParameterTypes 方法参数类型
     * *
     * @return 给定的类中给定名称的字段的GET方法
     */
    fun getValueOfMethod(sourceClass: Class<*>, vararg methodParameterTypes: Class<*>): Method? {
        return getMethod(sourceClass, true, true, "valueOf", *methodParameterTypes)
    }

    /**
     * 调用不带参数的方法

     * @param method
     * *
     * @param object
     * *
     * @return
     * *
     * @throws Exception
     */
    @Throws(Exception::class)
    fun invokeMethod(method: Method, obj: Any): Any {
        return method.invoke(obj, *EMPTY_PARAMS)
    }

    /* ************************************************** 构造函数相关的方法 ******************************************************* */

    /**
     * 获取给定的类中给定参数类型的构造函数

     * @param sourceClass               给定的类
     * *
     * @param isFindDeclaredConstructor 是否查找Declared构造函数
     * *
     * @param isUpwardFind              是否向上去其父类中寻找
     * *
     * @param constructorParameterTypes 构造函数的参数类型
     * *
     * @return 给定的类中给定参数类型的构造函数
     */
    fun getConstructor(sourceClass: Class<*>, isFindDeclaredConstructor: Boolean, isUpwardFind: Boolean, vararg constructorParameterTypes: Class<*>): Constructor<*>? {
        var method: Constructor<*>? = null
        try {
            method = if (isFindDeclaredConstructor) sourceClass.getDeclaredConstructor(*constructorParameterTypes) else sourceClass.getConstructor(*constructorParameterTypes)
        } catch (e1: NoSuchMethodException) {
            if (isUpwardFind) {
                var classs: Class<*>? = sourceClass.superclass
                while (method == null && classs != null) {
                    try {
                        method = if (isFindDeclaredConstructor) sourceClass.getDeclaredConstructor(*constructorParameterTypes) else sourceClass.getConstructor(*constructorParameterTypes)
                    } catch (e11: NoSuchMethodException) {
                        classs = classs.superclass
                    }

                }
            }
        }

        return method
    }

    /**
     * 获取给定的类中所有的构造函数

     * @param sourceClass               给定的类
     * *
     * @param isFindDeclaredConstructor 是否需要获取Declared构造函数
     * *
     * @param isFromSuperClassGet       是否需要把其父类中的构造函数也取出
     * *
     * @param isDESCGet                 在最终获取的列表里，父类的构造函数是否需要排在子类的前面。只有需要把其父类中的构造函数也取出时此参数才有效
     * *
     * @return 给定的类中所有的构造函数
     */
    fun getConstructors(sourceClass: Class<*>, isFindDeclaredConstructor: Boolean, isFromSuperClassGet: Boolean, isDESCGet: Boolean): List<Constructor<*>> {
        val constructorList = ArrayList<Constructor<*>>()
        //如果需要从父类中获取
        if (isFromSuperClassGet) {
            //获取当前类的所有父类
            val classList = getSuperClasss(sourceClass, true)

            //如果是降序获取
            if (isDESCGet) {
                for (w in classList.size - 1 downTo -1 + 1) {
                    for (constructor in if (isFindDeclaredConstructor) classList[w].declaredConstructors else classList[w].constructors) {
                        constructorList.add(constructor)
                    }
                }
            } else {
                for (w in classList.indices) {
                    for (constructor in if (isFindDeclaredConstructor) classList[w].declaredConstructors else classList[w].constructors) {
                        constructorList.add(constructor)
                    }
                }
            }
        } else {
            for (constructor in if (isFindDeclaredConstructor) sourceClass.declaredConstructors else sourceClass.constructors) {
                constructorList.add(constructor)
            }
        }
        return constructorList
    }


    /* ************************************************** 父类相关的方法 ******************************************************* */

    /**
     * 获取给定的类所有的父类

     * @param sourceClass       给定的类
     * *
     * @param isAddCurrentClass 是否将当年类放在最终返回的父类列表的首位
     * *
     * @return 给定的类所有的父类
     */
    fun getSuperClasss(sourceClass: Class<*>, isAddCurrentClass: Boolean): MutableList<Class<*>> {
        val classList = ArrayList<Class<*>>()
        var classs: Class<*>?
        if (isAddCurrentClass) {
            classs = sourceClass
        } else {
            classs = sourceClass.superclass
        }
        while (classs != null) {
            classList.add(classs)
            classs = classs.superclass
        }
        return classList
    }


    /* ************************************************** 其它的辅助方法 ******************************************************* */
    /**
     * 获取给定的类的名字

     * @param sourceClass 给定的类
     * *
     * @return 给定的类的名字
     */
    fun getClassName(sourceClass: Class<*>): String {
        val classPath = sourceClass.name
        return classPath.substring(classPath.lastIndexOf('.') + 1)
    }

    fun <T> getObjectByFieldName(`object`: Any?, fieldName: String, clas: Class<T>?): T? {
        if (`object` != null && !fieldName.isEmpty() && clas != null) {
            try {
                val field = ReflectUtils.getField(`object`.javaClass, fieldName, true, true)
                if (field != null) {
                    field.isAccessible = true
                    return field.get(`object`) as T
                } else {
                    return null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        } else {
            return null
        }
    }

    /**
     * 判断给定字段是否是type类型的数组

     * @param field
     * *
     * @param type
     * *
     * @return
     */
    fun isArrayByType(field: Field, type: Class<*>): Boolean {
        val fieldType = field.type
        return fieldType.isArray && type.isAssignableFrom(fieldType.componentType)
    }

    /**
     * 判断给定字段是否是type类型的collectionType集合，例如collectionType=List.class，type=Date.class就是要判断给定字段是否是Date类型的List

     * @param field
     * *
     * @param collectionType
     * *
     * @param type
     * *
     * @return
     */
    fun isCollectionByType(field: Field, collectionType: Class<out Collection<*>>, type: Class<*>): Boolean {
        val fieldType = field.type
        if (collectionType.isAssignableFrom(fieldType)) {
            val first = (field.genericType as ParameterizedType).actualTypeArguments[0] as Class<*>
            return type.isAssignableFrom(first)
        } else {
            return false
        }
    }
}
