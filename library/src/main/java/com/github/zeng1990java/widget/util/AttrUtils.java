package com.github.zeng1990java.widget.util;

/*
 * Copyright (C) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import ohos.agp.components.AttrSet;
import ohos.agp.components.element.Element;

/**
 * get attr value class
 */
public class AttrUtils {
    /**
     * get the int value from AttrSet
     *
     * @param attrs the attrSet
     * @param name the attrName
     * @param defaultValue the defaultValue
     * @return int value
     */
    public static int getIntFromAttr(AttrSet attrs, String name, int defaultValue) {
        int value = defaultValue;
        try {
            if (attrs.getAttr(name).isPresent() && attrs.getAttr(name).get() != null) {
                value = attrs.getAttr(name).get().getIntegerValue();
            }
        } catch (Exception e) {
            return value;
        }
        return value;
    }

    /**
     * get the float value from AttrSet
     *
     * @param attrs the attrSet
     * @param name the attrName
     * @param defaultValue the defaultValue
     * @return float value
     */
    public static float getFloatFromAttr(AttrSet attrs, String name, float defaultValue) {
        float value = defaultValue;
        try {
            if (attrs.getAttr(name).isPresent() && attrs.getAttr(name).get() != null) {
                value = attrs.getAttr(name).get().getFloatValue();
            }
        } catch (Exception e) {
            return value;
        }
        return value;
    }

    /**
     * get the boolean value from AttrSet
     *
     * @param attrs the attrSet
     * @param name the attrName
     * @param defaultValue the defaultValue
     * @return boolean value
     */
    public static boolean getBooleanFromAttr(AttrSet attrs, String name, boolean defaultValue) {
        boolean value = defaultValue;
        try {
            if (attrs.getAttr(name).isPresent() && attrs.getAttr(name).get() != null) {
                value = attrs.getAttr(name).get().getBoolValue();
            }
        } catch (Exception e) {
            return value;
        }
        return value;
    }

    /**
     * get the long value from AttrSet
     *
     * @param attrs the attrSet
     * @param name the attrName
     * @param defaultValue the defaultValue
     * @return long value
     */
    public static long getLongFromAttr(AttrSet attrs, String name, long defaultValue) {
        long value = defaultValue;
        try {
            if (attrs.getAttr(name).isPresent() && attrs.getAttr(name).get() != null) {
                value = attrs.getAttr(name).get().getLongValue();
            }
        } catch (Exception e) {
            return value;
        }
        return value;
    }

    /**
     * get the color value from AttrSet
     *
     * @param attrs the attrSet
     * @param name the attrName
     * @param defaultValue the defaultValue
     * @return int colorValue
     */
    public static int getColorFromAttr(AttrSet attrs, String name, int defaultValue) {
        int value = defaultValue;
        try {
            if (attrs.getAttr(name).isPresent() && attrs.getAttr(name).get() != null) {
                value = attrs.getAttr(name).get().getColorValue().getValue();
            }
        } catch (Exception e) {
            return value;
        }
        return value;
    }

    /**
     * get the dimensionValue value from AttrSet
     *
     * @param attrs the attrSet
     * @param name the attrName
     * @param defaultValue the defaultValue
     * @return int dimensionValue
     */
    public static int getDimensionFromAttr(AttrSet attrs, String name, int defaultValue) {
        int value = defaultValue;
        try {
            if (attrs.getAttr(name).isPresent() && attrs.getAttr(name).get() != null) {
                value = attrs.getAttr(name).get().getDimensionValue();
            }
        } catch (Exception e) {
            return value;
        }
        return value;
    }

    /**
     * get the String value from AttrSet
     *
     * @param attrs the attrSet
     * @param name the attrName
     * @param defaultValue the defaultValue
     * @return String value
     */
    public static String getStringFromAttr(AttrSet attrs, String name, String defaultValue) {
        String value = defaultValue;
        try {
            if (attrs.getAttr(name).isPresent() && attrs.getAttr(name).get() != null) {
                value = attrs.getAttr(name).get().getStringValue();
            }
        } catch (Exception e) {
            return value;
        }
        return value;
    }

    /**
     * get the Element value from AttrSet
     *
     * @param attrs the attrSet
     * @param name the attrName
     * @param defaultValue the defaultValue
     * @return Element value
     */
    public static Element getElementFromAttr(AttrSet attrs, String name, Element defaultValue) {
        Element value = defaultValue;
        try {
            if (attrs.getAttr(name).isPresent() && attrs.getAttr(name).get() != null) {
                value = attrs.getAttr(name).get().getElement();
            }
        } catch (Exception e) {
            return value;
        }
        return value;
    }
}
