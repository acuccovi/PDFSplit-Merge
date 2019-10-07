/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cuccovillo.alessio.pdfsplitmerge.model;

import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;

public class Bookmark {

    private String title;
    private int firstPage;
    private int lastPage;

    public Bookmark(String title, PDPageDestination destination) {
        this.title = title;
        this.firstPage = destination.retrievePageNumber();
        this.lastPage = -1;
    }

    public Bookmark(String title, int firstPage, int lastPage) {
        this.title = title;
        this.firstPage = firstPage;
        this.lastPage = lastPage;
    }

    public String getTitle() {
        return title;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    @Override
    public String toString() {
        return String.format("%s [%s page(s)]", getTitle(), getLastPage() - getFirstPage());
    }

}
