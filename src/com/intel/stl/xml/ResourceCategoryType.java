/**
 * Copyright (c) 2015, Intel Corporation
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Intel Corporation nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.intel.stl.xml;

import javax.xml.bind.TypeConstraintException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import com.intel.stl.api.configuration.ResourceCategory;
import com.intel.stl.api.configuration.ResourceType;

/**
 * <p>
 * Java class for ResourceCategoryType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="ResourceCategoryType">
 *   &lt;simpleContent>
 *     &lt;extension base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceCategoryType", propOrder = { "value" })
@XmlSeeAlso({ PortCategoryType.class, HfiCategoryType.class,
        SwitchCategoryType.class })
public abstract class ResourceCategoryType {

    @XmlValue
    protected String value;

    @XmlAttribute
    protected Boolean showHeader;

    @XmlAttribute
    protected String valueHeader;

    /**
     * Gets the value of the value property. This value is used as the key
     * header for this resource category
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property. This value is used as the key
     * header for this resource category
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the showHeader property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public boolean isShowHeader() {
        if (showHeader == null) {
            return true;
        } else {
            return showHeader;
        }
    }

    /**
     * Sets the value of the showHeader property.
     * 
     * @param value
     *            allowed object is {@link boolean }
     * 
     */
    public void setShowHeader(Boolean value) {
        this.showHeader = value;
    }

    /**
     * Gets the value of the valueHeader property. This value is used as the
     * value header for this resource category
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getValueHeader() {
        return valueHeader;
    }

    /**
     * Sets the value of the valueHeader property. This value is used as the
     * value header for this resource category
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setValueHeader(String value) {
        this.valueHeader = value;
    }

    /**
     * Gets the corresponding resource category for this ResourceCategoryType
     * 
     * @return possible object is {@link ResourceCategory }
     * 
     */
    protected abstract ResourceCategory getResourceCategory();

    protected ResourceCategory getPropertyCategoryFor(
            ResourceType resourceType, String categoryName) {
        ResourceCategory category =
                ResourceCategory.getResourceCategoryFor(categoryName);
        if (category == null) {
            TypeConstraintException tce =
                    new TypeConstraintException("Category name '"
                            + categoryName + "' (" + resourceType.name()
                            + ") has no matching PropertyCategory");
            throw tce;
        }
        if (!category.isApplicableTo(resourceType)) {
            TypeConstraintException tce =
                    new TypeConstraintException("Category name '"
                            + categoryName
                            + "' is not applicable to resource type "
                            + resourceType.name());
            throw tce;
        }
        return category;
    }

}
