(ns portable-text.html-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [portable-text.html :as sut]))

(deftest empty-block-001
  (is (= (sut/render
          {:_key "R5FvMrjo",
           :_type "block",
           :children [],
           :markDefs [],
           :style "normal"})
         "<p></p>")))

(deftest single-span-002
  (is (= (sut/render
          {:_key "R5FvMrjo",
           :_type "block",
           :children
           [{:_key "cZUQGmh4", :_type "span", :marks [], :text "Plain text."}],
           :markDefs [],
           :style "normal"})
         "<p>Plain text.</p>")))

(deftest multiple-spans-003
  (is (= (sut/render
          {:_key "R5FvMrjo",
           :_type "block",
           :children
           [{:_key "cZUQGmh4",
             :_type "span",
             :marks [],
             :text "Span number one. "}
            {:_key "toaiCqIK",
             :_type "span",
             :marks [],
             :text "And span number two."}],
           :markDefs [],
           :style "normal"})
         "<p>Span number one. And span number two.</p>")))

(deftest basic-mark-single-span-004
  (is (= (sut/render
          {:_key "R5FvMrjo",
           :_type "block",
           :children
           [{:_key "cZUQGmh4",
             :_type "span",
             :marks ["code"],
             :text "sanity"}
            {:_key "toaiCqIK",
             :_type "span",
             :marks [],
             :text " is the name of the CLI tool."}],
           :markDefs [],
           :style "normal"})
         "<p><code>sanity</code> is the name of the CLI tool.</p>")))

(deftest basic-mark-multiple-adjacent-spans-005
  (is (= (sut/render
          {:_key "R5FvMrjo",
           :_type "block",
           :children
           [{:_key "cZUQGmh4",
             :_type "span",
             :marks ["strong"],
             :text "A word of"}
            {:_key "toaiCqIK",
             :_type "span",
             :marks ["strong"],
             :text " warning;"}
            {:_key "gaZingA",
             :_type "span",
             :marks [],
             :text " Sanity is addictive."}],
           :markDefs [],
           :style "normal"})
         "<p><strong>A word of warning;</strong> Sanity is addictive.</p>")))

(deftest basic-mark-nested-marks-006
  (is (= (sut/render
          {:_key "R5FvMrjo",
           :_type "block",
           :children
           [{:_key "cZUQGmh4",
             :_type "span",
             :marks ["strong"],
             :text "A word of "}
            {:_key "toaiCqIK",
             :_type "span",
             :marks ["strong" "em"],
             :text "warning;"}
            {:_key "gaZingA",
             :_type "span",
             :marks [],
             :text " Sanity is addictive."}],
           :markDefs [],
           :style "normal"})
         "<p><strong>A word of <em>warning;</em></strong> Sanity is addictive.</p>")))

(deftest link-mark-def-007
  (is (= (sut/render
          {:_key "R5FvMrjo",
           :_type "block",
           :children
           [{:_key "cZUQGmh4",
             :_type "span",
             :marks [],
             :text "A word of warning; "}
            {:_key "toaiCqIK",
             :_type "span",
             :marks ["someLinkId"],
             :text "Sanity"}
            {:_key "gaZingA",
             :_type "span",
             :marks [],
             :text " is addictive."}],
           :markDefs
           [{:_type "link",
             :_key "someLinkId",
             :href "https://sanity.io/"}],
           :style "normal"})
         "<p>A word of warning; <a href=\"https://sanity.io/\">Sanity</a> is addictive.</p>")))

(deftest plain-header-block-008
  (is (= (sut/render
          {:_key "R5FvMrjo",
           :_type "block",
           :children
           [{:_key "cZUQGmh4", :_type "span", :marks [], :text "Dat heading"}],
           :markDefs [],
           :style "h2"})
         "<h2>Dat heading</h2>")))

(deftest messy-link-text-009
  (is (= (sut/render
          {:_type "block",
           :children
           [{:_key "a1ph4", :_type "span", :marks ["zomgLink"], :text "Sanity"}
            {:_key "b374",
             :_type "span",
             :marks [],
             :text " can be used to power almost any "}
            {:_key "ch4r1i3",
             :_type "span",
             :marks ["zomgLink" "strong" "em"],
             :text "app"}
            {:_key "d3174",
             :_type "span",
             :marks ["em" "zomgLink"],
             :text " or website"}
            {:_key "ech0", :_type "span", :marks [], :text "."}],
           :markDefs
           [{:_key "zomgLink", :_type "link", :href "https://sanity.io/"}],
           :style "blockquote"})
         "<blockquote><a href=\"https://sanity.io/\">Sanity</a> can be used to power almost any <a href=\"https://sanity.io/\"><em><strong>app</strong> or website</em></a>.</blockquote>")))

(deftest basic-bullet-list-010
  (is (= (sut/render
          [{:style "normal",
            :_type "block",
            :_key "f94596b05b41",
            :markDefs [],
            :children
            [{:_type "span",
              :text "Let's test some of these lists!",
              :marks []}]}
           {:listItem "bullet",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "937effb1cd06",
            :markDefs [],
            :children [{:_type "span", :text "Bullet 1", :marks []}]}
           {:listItem "bullet",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "bd2d22278b88",
            :markDefs [],
            :children [{:_type "span", :text "Bullet 2", :marks []}]}
           {:listItem "bullet",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "a97d32e9f747",
            :markDefs [],
            :children [{:_type "span", :text "Bullet 3", :marks []}]}])
         "<div><p>Let&#x27;s test some of these lists!</p><ul><li>Bullet 1</li><li>Bullet 2</li><li>Bullet 3</li></ul></div>")))

(deftest basic-numbered-list-011
  (is (= (sut/render
          [{:style "normal",
            :_type "block",
            :_key "f94596b05b41",
            :markDefs [],
            :children
            [{:_type "span",
              :text "Let's test some of these lists!",
              :marks []}]}
           {:listItem "number",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "937effb1cd06",
            :markDefs [],
            :children [{:_type "span", :text "Number 1", :marks []}]}
           {:listItem "number",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "bd2d22278b88",
            :markDefs [],
            :children [{:_type "span", :text "Number 2", :marks []}]}
           {:listItem "number",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "a97d32e9f747",
            :markDefs [],
            :children [{:_type "span", :text "Number 3", :marks []}]}])
         "<div><p>Let&#x27;s test some of these lists!</p><ol><li>Number 1</li><li>Number 2</li><li>Number 3</li></ol></div>")))

(deftest image-support-012
  (is (= (sut/render
          [{:style "normal",
            :_type "block",
            :_key "bd73ec5f61a1",
            :markDefs [],
            :children
            [{:_type "span",
              :text "Also, images are pretty common.",
              :marks []}]}
           {:_type "image",
            :_key "d234a4fa317a",
            :asset
            {:_type "reference",
             :_ref "image-YiOKD0O6AdjKPaK24WtbOEv0-3456x2304-jpg"}}]
          {:sanity/project-id "3do82whm"
           :sanity/dataset "production"})
         "<div><p>Also, images are pretty common.</p><figure><img src=\"https://cdn.sanity.io/images/3do82whm/production/YiOKD0O6AdjKPaK24WtbOEv0-3456x2304.jpg\"/></figure></div>")))

(deftest materialized-image-support-013
  (is (= (sut/render
          [{:style "normal",
            :_type "block",
            :_key "bd73ec5f61a1",
            :markDefs [],
            :children
            [{:_type "span",
              :text "Also, images are pretty common.",
              :marks []}]}
           {:_key "bd45080bf448",
            :_type "image",
            :asset
            {:path
             "images/3do82whm/production/caC3MscJLd3mNAbMdQ6-5748x3832.jpg",
             :_id "image-caC3MscJLd3mNAbMdQ6-5748x3832-jpg",
             :_createdAt "2017-08-02T23:04:57Z",
             :_updatedAt "2017-09-19T18:05:06Z",
             :_rev "ch7HXy1Ux9jmVKZ6TKPoZ8",
             :_type "sanity.imageAsset",
             :assetId "caC3MscJLd3mNAbMdQ6",
             :extension "jpg",
             :mimeType "image/jpeg",
             :url
             "https://cdn.sanity.io/images/3do82whm/production/caC3MscJLd3mNAbMdQ6-5748x3832.jpg",
             :metadata
             {:dimensions {:aspectRatio 1.5, :height 3832, :width 5748}}}}])
         "<div><p>Also, images are pretty common.</p><figure><img src=\"https://cdn.sanity.io/images/3do82whm/production/caC3MscJLd3mNAbMdQ6-5748x3832.jpg\"/></figure></div>")))

(deftest nested-lists-014
  (is (= (sut/render
          [{:_type "block",
            :_key "a",
            :markDefs [],
            :style "normal",
            :children [{:_type "span", :marks [], :text "Span"}]}
           {:_type "block",
            :_key "b",
            :markDefs [],
            :level 1,
            :children [{:_type "span", :marks [], :text "Item 1, level 1"}],
            :listItem "bullet"}
           {:_type "block",
            :_key "c",
            :markDefs [],
            :level 1,
            :children [{:_type "span", :marks [], :text "Item 2, level 1"}],
            :listItem "bullet"}
           {:_type "block",
            :_key "d",
            :markDefs [],
            :level 2,
            :children [{:_type "span", :marks [], :text "Item 3, level 2"}],
            :listItem "number"}
           {:_type "block",
            :_key "e",
            :markDefs [],
            :level 3,
            :children [{:_type "span", :marks [], :text "Item 4, level 3"}],
            :listItem "number"}
           {:_type "block",
            :_key "f",
            :markDefs [],
            :level 2,
            :children [{:_type "span", :marks [], :text "Item 5, level 2"}],
            :listItem "number"}
           {:_type "block",
            :_key "g",
            :markDefs [],
            :level 2,
            :children [{:_type "span", :marks [], :text "Item 6, level 2"}],
            :listItem "number"}
           {:_type "block",
            :_key "h",
            :markDefs [],
            :level 1,
            :children [{:_type "span", :marks [], :text "Item 7, level 1"}],
            :listItem "bullet"}
           {:_type "block",
            :_key "i",
            :markDefs [],
            :level 1,
            :children [{:_type "span", :marks [], :text "Item 8, level 1"}],
            :listItem "bullet"}
           {:_type "block",
            :_key "j",
            :markDefs [],
            :level 1,
            :children [{:_type "span", :marks [], :text "Item 1 of list 2"}],
            :listItem "number"}
           {:_type "block",
            :_key "k",
            :markDefs [],
            :level 1,
            :children [{:_type "span", :marks [], :text "Item 2 of list 2"}],
            :listItem "number"}
           {:_type "block",
            :_key "l",
            :markDefs [],
            :level 2,
            :children
            [{:_type "span", :marks [], :text "Item 3 of list 2, level 2"}],
            :listItem "number"}
           {:_type "block",
            :_key "m",
            :markDefs [],
            :style "normal",
            :children [{:_type "span", :marks [], :text "Just a block"}]}])
         "<div><p>Span</p><ul><li>Item 1, level 1</li><li>Item 2, level 1<ol><li>Item 3, level 2<ol><li>Item 4, level 3</li></ol></li><li>Item 5, level 2</li><li>Item 6, level 2</li></ol></li><li>Item 7, level 1</li><li>Item 8, level 1</li></ul><ol><li>Item 1 of list 2</li><li>Item 2 of list 2<ol><li>Item 3 of list 2, level 2</li></ol></li></ol><p>Just a block</p></div>")))

(deftest all-basic-marks-015
  (is (= (sut/render
          {:_key "R5FvMrjo",
           :_type "block",
           :children
           [{:_key "a", :_type "span", :marks ["code"], :text "code"}
            {:_key "b", :_type "span", :marks ["strong"], :text "strong"}
            {:_key "c", :_type "span", :marks ["em"], :text "em"}
            {:_key "d", :_type "span", :marks ["underline"], :text "underline"}
            {:_key "e",
             :_type "span",
             :marks ["strike-through"],
             :text "strike-through"}
            {:_key "f", :_type "span", :marks ["dat-link"], :text "link"}],
           :markDefs
           [{:_key "dat-link", :_type "link", :href "https://www.sanity.io/"}],
           :style "normal"})
         "<p><code>code</code><strong>strong</strong><em>em</em><span style=\"text-decoration:underline;\">underline</span><del>strike-through</del><a href=\"https://www.sanity.io/\">link</a></p>")))

(deftest deep-weird-lists-016
  (is (= (sut/render
          [{:listItem "bullet",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "fde2e840a29c",
            :markDefs [],
            :children [{:_type "span", :text "Item a", :marks []}]}
           {:listItem "bullet",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "c16f11c71638",
            :markDefs [],
            :children [{:_type "span", :text "Item b", :marks []}]}
           {:listItem "number",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "e92f55b185ae",
            :markDefs [],
            :children [{:_type "span", :text "Item 1", :marks []}]}
           {:listItem "number",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "a77e71209aff",
            :markDefs [],
            :children [{:_type "span", :text "Item 2", :marks []}]}
           {:listItem "number",
            :style "normal",
            :level 2,
            :_type "block",
            :_key "da1f863df265",
            :markDefs [],
            :children [{:_type "span", :text "Item 2, a", :marks []}]}
           {:listItem "number",
            :style "normal",
            :level 2,
            :_type "block",
            :_key "60d8c92bed0d",
            :markDefs [],
            :children [{:_type "span", :text "Item 2, b", :marks []}]}
           {:listItem "number",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "6dbc061d5d36",
            :markDefs [],
            :children [{:_type "span", :text "Item 3", :marks []}]}
           {:style "normal",
            :_type "block",
            :_key "bb89bd1ef2c9",
            :markDefs [],
            :children [{:_type "span", :text "", :marks []}]}
           {:listItem "bullet",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "289c1f176eab",
            :markDefs [],
            :children [{:_type "span", :text "In", :marks []}]}
           {:listItem "bullet",
            :style "normal",
            :level 2,
            :_type "block",
            :_key "011f8cc6d19b",
            :markDefs [],
            :children [{:_type "span", :text "Out", :marks []}]}
           {:listItem "bullet",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "ccfb4e37b798",
            :markDefs [],
            :children [{:_type "span", :text "In", :marks []}]}
           {:listItem "bullet",
            :style "normal",
            :level 2,
            :_type "block",
            :_key "bd0102405e5c",
            :markDefs [],
            :children [{:_type "span", :text "Out", :marks []}]}
           {:listItem "bullet",
            :style "normal",
            :level 3,
            :_type "block",
            :_key "030fda546030",
            :markDefs [],
            :children [{:_type "span", :text "Even More", :marks []}]}
           {:listItem "bullet",
            :style "normal",
            :level 4,
            :_type "block",
            :_key "80369435aed0",
            :markDefs [],
            :children [{:_type "span", :text "Even deeper", :marks []}]}
           {:listItem "bullet",
            :style "normal",
            :level 2,
            :_type "block",
            :_key "3b36919a8914",
            :markDefs [],
            :children [{:_type "span", :text "Two steps back", :marks []}]}
           {:listItem "bullet",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "9193cbc6ba54",
            :markDefs [],
            :children [{:_type "span", :text "All the way back", :marks []}]}
           {:listItem "bullet",
            :style "normal",
            :level 3,
            :_type "block",
            :_key "256fe8487d7a",
            :markDefs [],
            :children [{:_type "span", :text "Skip a step", :marks []}]}
           {:listItem "number",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "aaa",
            :markDefs [],
            :children [{:_type "span", :text "New list", :marks []}]}
           {:listItem "number",
            :style "normal",
            :level 2,
            :_type "block",
            :_key "bbb",
            :markDefs [],
            :children [{:_type "span", :text "Next level", :marks []}]}
           {:listItem "bullet",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "ccc",
            :markDefs [],
            :children [{:_type "span", :text "New bullet list", :marks []}]}])
         "<div><ul><li>Item a</li><li>Item b</li></ul><ol><li>Item 1</li><li>Item 2<ol><li>Item 2, a</li><li>Item 2, b</li></ol></li><li>Item 3</li></ol><p></p><ul><li>In<ul><li>Out</li></ul></li><li>In<ul><li>Out<ul><li>Even More<ul><li>Even deeper</li></ul></li></ul></li><li>Two steps back</li></ul></li><li>All the way back<ul><li>Skip a step</li></ul></li></ul><ol><li>New list<ol><li>Next level</li></ol></li></ol><ul><li>New bullet list</li></ul></div>")))

(deftest all-default-block-styles-017
  (is (= (sut/render
          [{:style "h1",
            :_type "block",
            :_key "b07278ae4e5a",
            :markDefs [],
            :children [{:_type "span", :text "Sanity", :marks []}]}
           {:style "h2",
            :_type "block",
            :_key "0546428bbac2",
            :markDefs [],
            :children [{:_type "span", :text "The outline", :marks []}]}
           {:style "h3",
            :_type "block",
            :_key "34024674e160",
            :markDefs [],
            :children [{:_type "span", :text "More narrow details", :marks []}]}
           {:style "h4",
            :_type "block",
            :_key "06ca981a1d18",
            :markDefs [],
            :children [{:_type "span", :text "Even less thing", :marks []}]}
           {:style "h5",
            :_type "block",
            :_key "06ca98afnjkg",
            :markDefs [],
            :children [{:_type "span", :text "Small header", :marks []}]}
           {:style "h6",
            :_type "block",
            :_key "cc0afafn",
            :markDefs [],
            :children [{:_type "span", :text "Lowest thing", :marks []}]}
           {:style "blockquote",
            :_type "block",
            :_key "0ee0381658d0",
            :markDefs [],
            :children
            [{:_type "span", :text "A block quote of awesomeness", :marks []}]}
           {:style "normal",
            :_type "block",
            :_key "44fb584a634c",
            :markDefs [],
            :children
            [{:_type "span", :text "Plain old normal block", :marks []}]}
           {:_type "block",
            :_key "abcdefg",
            :markDefs [],
            :children
            [{:_type "span", :text "Default to \"normal\" style", :marks []}]}])
         "<div><h1>Sanity</h1><h2>The outline</h2><h3>More narrow details</h3><h4>Even less thing</h4><h5>Small header</h5><h6>Lowest thing</h6><blockquote>A block quote of awesomeness</blockquote><p>Plain old normal block</p><p>Default to &quot;normal&quot; style</p></div>")))

(deftest marks-all-the-way-down-018
  (is (= (sut/render
          {:_type "block",
           :children
           [{:_key "a1ph4",
             :_type "span",
             :marks ["mark1" "mark2" "em"],
             :text "Sanity"}
            {:_key "b374",
             :_type "span",
             :marks ["mark2" "mark1" "em"],
             :text " FTW"}],
           :markDefs
           [{:_key "mark1", :_type "highlight", :thickness 1}
            {:_key "mark2", :_type "highlight", :thickness 3}]})
         "<p><span style=\"border:1px solid;\"><span style=\"border:3px solid;\"><em>Sanity FTW</em></span></span></p>")))

(deftest keyless-019
  (is (= (sut/render
          [{:_type "block",
            :children
            [{:_type "span", :marks [], :text "sanity"}
             {:_type "span", :marks [], :text " is a full time job"}],
            :markDefs [],
            :style "normal"}
           {:_type "block",
            :children
            [{:_type "span", :marks [], :text "in a world that "}
             {:_type "span", :marks [], :text "is always changing"}],
            :markDefs [],
            :style "normal"}])
         "<div><p>sanity is a full time job</p><p>in a world that is always changing</p></div>")))

(deftest empty-array-020
  (is (= (sut/render
          [])
         "")))

(deftest list-without-level-021
  (is (= (sut/render
          [{:_key "e3ac53b5b339",
            :_type "block",
            :children
            [{:_type "span",
              :marks [],
              :text "In-person access: Research appointments"}],
            :markDefs [],
            :style "h2"}
           {:_key "a25f0be55c47",
            :_type "block",
            :children
            [{:_type "span",
              :marks [],
              :text
              "The collection may be examined by arranging a research appointment "}
             {:_type "span", :marks ["strong"], :text "in advance"}
             {:_type "span",
              :marks [],
              :text
              " by contacting the ACT archivist by email or phone. ACT generally does not accept walk-in research patrons, although requests may be made in person at the Archivist’s office (E15-222). ACT recommends arranging appointments at least three weeks in advance in order to ensure availability. ACT reserves the right to cancel research appointments at any time. Appointment scheduling is subject to institute holidays and closings. "}],
            :markDefs [],
            :style "normal"}
           {:_key "9490a3085498",
            :_type "block",
            :children
            [{:_type "span",
              :marks [],
              :text
              "The collection space is located at:\n20 Ames Street\nBuilding E15-235\nCambridge, Massachusetts 02139"}],
            :markDefs [],
            :style "normal"}
           {:_key "4c37f3bc1d71",
            :_type "block",
            :children
            [{:_type "span",
              :marks [],
              :text "In-person access: Space policies"}],
            :markDefs [],
            :style "h2"}
           {:_key "a77cf4905e83",
            :_type "block",
            :children
            [{:_type "span",
              :marks [],
              :text
              "The Archivist or an authorized ACT staff member must attend researchers at all times."}],
            :listItem "bullet",
            :markDefs [],
            :style "normal"}
           {:_key "9a039c533554",
            :_type "block",
            :children
            [{:_type "span",
              :marks [],
              :text
              "No pens, markers, or adhesives (e.g. “Post-it” notes) are permitted in the collection space; pencils will be provided upon request."}],
            :listItem "bullet",
            :markDefs [],
            :style "normal"}
           {:_key "beeee9405136",
            :_type "block",
            :children
            [{:_type "span",
              :marks [],
              :text
              "Cotton gloves must be worn when handling collection materials; gloves will be provided by the Archivist."}],
            :listItem "bullet",
            :markDefs [],
            :style "normal"}
           {:_key "8b78daa65d60",
            :_type "block",
            :children
            [{:_type "span",
              :marks [],
              :text
              "No food or beverages are permitted in the collection space."}],
            :listItem "bullet",
            :markDefs [],
            :style "normal"}
           {:_key "d0188e00a887",
            :_type "block",
            :children
            [{:_type "span",
              :marks [],
              :text
              "Laptop use is permitted in the collection space, as well as digital cameras and cellphones. Unless otherwise authorized, any equipment in the collection space (including but not limited to computers, telephones, scanners, and viewing equipment) is for use by ACT staff members only."}],
            :listItem "bullet",
            :markDefs [],
            :style "normal"}
           {:_key "06486dd9e1c6",
            :_type "block",
            :children
            [{:_type "span",
              :marks [],
              :text
              "Photocopying machines in the ACT hallway will be accessible by patrons under the supervision of the Archivist."}],
            :listItem "bullet",
            :markDefs [],
            :style "normal"}
           {:_key "e6f6f5255fb6",
            :_type "block",
            :children
            [{:_type "span",
              :marks [],
              :text
              "Patrons may only browse materials that have been made available for access."}],
            :listItem "bullet",
            :markDefs [],
            :style "normal"}
           {:_key "99b3e265fa02",
            :_type "block",
            :children
            [{:_type "span",
              :marks [],
              :text "Remote access: Reference requests"}],
            :markDefs [],
            :style "h2"}
           {:_key "ea13459d9e46",
            :_type "block",
            :children
            [{:_type "span",
              :marks [],
              :text
              "For patrons who are unable to arrange for an on-campus visit to the Archives and Special Collections, reference questions may be directed to the Archivist remotely by email or phone. Generally, emails and phone calls will receive a response within 72 hours of receipt. Requests are typically filled in the order they are received."}],
            :markDefs [],
            :style "normal"}
           {:_key "100958e35c94",
            :_type "block",
            :children
            [{:_type "span",
              :marks ["strong"],
              :text "Use of patron information"}],
            :markDefs [],
            :style "h2"}
           {:_key "2e0dde67b7df",
            :_type "block",
            :children
            [{:_type "span",
              :marks [],
              :text
              "Patrons requesting collection materials in person or remotely may be asked to provide certain information to the Archivist, such as contact information and topic(s) of research. This information is only used to track requests for statistical evaluations of collection use and will not be disclosed to outside organizations for any purpose. ACT will endeavor to protect the privacy of all patrons accessing collections."}],
            :markDefs [],
            :style "normal"}
           {:_key "8f39a1ec6366",
            :_type "block",
            :children [{:_type "span", :marks ["strong"], :text "Fees"}],
            :markDefs [],
            :style "h2"}
           {:_key "090062c9e8ce",
            :_type "block",
            :children
            [{:_type "span",
              :marks [],
              :text
              "ACT reserves the right to charge an hourly rate for requests that require more than three hours of research on behalf of a patron (remote requests). Collection materials may be scanned and made available upon request, but digitization of certain materials may incur costs. Additionally, requests to publish, exhibit, or otherwise reproduce and display collection materials may incur use fees."}],
            :markDefs [],
            :style "normal"}
           {:_key "e2b58e246069",
            :_type "block",
            :children
            [{:_type "span",
              :marks ["strong"],
              :text "Use of MIT-owned materials by patrons"}],
            :markDefs [],
            :style "h2"}
           {:_key "7cedb6800dc6",
            :_type "block",
            :children
            [{:_type "span",
              :marks [],
              :text
              "Permission to examine collection materials in person or remotely (by receiving transfers of digitized materials) does not imply or grant permission to publish or exhibit those materials. Permission to publish, exhibit, or otherwise use collection materials is granted on a case by case basis in accordance with MIT policy, restrictions that may have been placed on materials by donors or depositors, and copyright law. To request permission to publish, exhibit, or otherwise use collection materials, contact the Archivist. "}
             {:_type "span",
              :marks ["strong"],
              :text
              "When permission is granted by MIT, patrons must comply with all guidelines provided by ACT for citations, credits, and copyright statements. Exclusive rights to examine or publish material will not be granted."}],
            :markDefs [],
            :style "normal"}])
         "<div><h2>In-person access: Research appointments</h2><p>The collection may be examined by arranging a research appointment <strong>in advance</strong> by contacting the ACT archivist by email or phone. ACT generally does not accept walk-in research patrons, although requests may be made in person at the Archivist’s office (E15-222). ACT recommends arranging appointments at least three weeks in advance in order to ensure availability. ACT reserves the right to cancel research appointments at any time. Appointment scheduling is subject to institute holidays and closings. </p><p>The collection space is located at:<br/>20 Ames Street<br/>Building E15-235<br/>Cambridge, Massachusetts 02139</p><h2>In-person access: Space policies</h2><ul><li>The Archivist or an authorized ACT staff member must attend researchers at all times.</li><li>No pens, markers, or adhesives (e.g. “Post-it” notes) are permitted in the collection space; pencils will be provided upon request.</li><li>Cotton gloves must be worn when handling collection materials; gloves will be provided by the Archivist.</li><li>No food or beverages are permitted in the collection space.</li><li>Laptop use is permitted in the collection space, as well as digital cameras and cellphones. Unless otherwise authorized, any equipment in the collection space (including but not limited to computers, telephones, scanners, and viewing equipment) is for use by ACT staff members only.</li><li>Photocopying machines in the ACT hallway will be accessible by patrons under the supervision of the Archivist.</li><li>Patrons may only browse materials that have been made available for access.</li></ul><h2>Remote access: Reference requests</h2><p>For patrons who are unable to arrange for an on-campus visit to the Archives and Special Collections, reference questions may be directed to the Archivist remotely by email or phone. Generally, emails and phone calls will receive a response within 72 hours of receipt. Requests are typically filled in the order they are received.</p><h2><strong>Use of patron information</strong></h2><p>Patrons requesting collection materials in person or remotely may be asked to provide certain information to the Archivist, such as contact information and topic(s) of research. This information is only used to track requests for statistical evaluations of collection use and will not be disclosed to outside organizations for any purpose. ACT will endeavor to protect the privacy of all patrons accessing collections.</p><h2><strong>Fees</strong></h2><p>ACT reserves the right to charge an hourly rate for requests that require more than three hours of research on behalf of a patron (remote requests). Collection materials may be scanned and made available upon request, but digitization of certain materials may incur costs. Additionally, requests to publish, exhibit, or otherwise reproduce and display collection materials may incur use fees.</p><h2><strong>Use of MIT-owned materials by patrons</strong></h2><p>Permission to examine collection materials in person or remotely (by receiving transfers of digitized materials) does not imply or grant permission to publish or exhibit those materials. Permission to publish, exhibit, or otherwise use collection materials is granted on a case by case basis in accordance with MIT policy, restrictions that may have been placed on materials by donors or depositors, and copyright law. To request permission to publish, exhibit, or otherwise use collection materials, contact the Archivist. <strong>When permission is granted by MIT, patrons must comply with all guidelines provided by ACT for citations, credits, and copyright statements. Exclusive rights to examine or publish material will not be granted.</strong></p></div>")))

(deftest inline-nodes-022
  (is (= (sut/render
          [{:_type "block",
            :_key "bd73ec5f61a1",
            :style "normal",
            :markDefs [],
            :children
            [{:_type "span", :text "Also, images are pretty common: ", :marks []}
             {:_type "image",
              :_key "d234a4fa317a",
              :asset
              {:_type "reference",
               :_ref "image-YiOKD0O6AdjKPaK24WtbOEv0-3456x2304-jpg"}}
             {:_type "span",
              :text " - as you can see, they can also appear inline!",
              :marks []}]}
           {:_type "block",
            :_key "foo",
            :markDefs [],
            :children [{:_type "span", :text "Sibling paragraph", :marks []}]}]
          {:sanity/project-id "3do82whm"
           :sanity/dataset "production"})
         "<div><p>Also, images are pretty common: <img src=\"https://cdn.sanity.io/images/3do82whm/production/YiOKD0O6AdjKPaK24WtbOEv0-3456x2304.jpg\"/> - as you can see, they can also appear inline!</p><p>Sibling paragraph</p></div>")))

(deftest hard-breaks-023
  (is (= (sut/render
          [{:_type "block",
            :_key "bd73ec5f61a1",
            :style "normal",
            :markDefs [],
            :children
            [{:_type "span",
              :text "A paragraph\ncan have hard\n\nbreaks.",
              :marks []}]}])
         "<p>A paragraph<br/>can have hard<br/><br/>breaks.</p>")))

(deftest inline-images-024
  (is (= (sut/render
          [{:_key "08707ed2945b",
            :_type "block",
            :style "normal",
            :children
            [{:_key "08707ed2945b0",
              :text "Foo! Bar!",
              :_type "span",
              :marks ["code"]}
             {:_key "a862cadb584f",
              :_type "image",
              :asset
              {:_ref "image-magnificent_beastZ8Z5qZHHxgrTJf6Hhz-162x120-png",
               :_type "reference"}}
             {:_key "08707ed2945b1", :text "Neat", :_type "span", :marks []}],
            :markDefs []}
           {:_key "abc",
            :_type "block",
            :style "normal",
            :children
            [{:_key "08707ed2945b0",
              :text "Foo! Bar! ",
              :_type "span",
              :marks ["code"]}
             {:_key "a862cadb584f",
              :_type "image",
              :asset
              {:_ref "image-magnificent_beastZ8Z5qZHHxgrTJf6Hhz-162x120-png",
               :_type "reference"}}
             {:_key "08707ed2945b1",
              :text " Baz!",
              :_type "span",
              :marks ["code"]}],
            :markDefs []}
           {:_key "def",
            :_type "block",
            :style "normal",
            :children
            [{:_key "08707ed2945b0",
              :text "Foo! Bar! ",
              :_type "span",
              :marks []}
             {:_key "a862cadb584f",
              :_type "image",
              :asset
              {:_ref "image-magnificent_beastZ8Z5qZHHxgrTJf6Hhz-162x120-png",
               :_type "reference"}}
             {:_key "08707ed2945b1",
              :text " Baz!",
              :_type "span",
              :marks ["code"]}],
            :markDefs []}]
          {:sanity/project-id "3do82whm"
           :sanity/dataset "production"})
         "<div><p><code>Foo! Bar!</code><img src=\"https://cdn.sanity.io/images/3do82whm/production/magnificent_beastZ8Z5qZHHxgrTJf6Hhz-162x120.png\"/>Neat</p><p><code>Foo! Bar! </code><img src=\"https://cdn.sanity.io/images/3do82whm/production/magnificent_beastZ8Z5qZHHxgrTJf6Hhz-162x120.png\"/><code> Baz!</code></p><p>Foo! Bar! <img src=\"https://cdn.sanity.io/images/3do82whm/production/magnificent_beastZ8Z5qZHHxgrTJf6Hhz-162x120.png\"/><code> Baz!</code></p></div>")))

(comment
  ;; Not yet supported
  (deftest image-with-hotspot-025
    (is (= (sut/render
            [{:style "normal",
              :_type "block",
              :_key "bd73ec5f61a1",
              :markDefs [],
              :children
              [{:_type "span",
                :text "Also, images are pretty common.",
                :marks []}]}
             {:_type "image",
              :_key "53811e851487",
              :asset
              {:_type "reference",
               :_ref
               "image-c2f0fc30003e6d7c79dcb5338a9b3d297cab4a8a-2000x1333-jpg"},
              :crop
              {:top 0.0960960960960961,
               :bottom 0.09609609609609615,
               :left 0.2340000000000001,
               :right 0.2240000000000001},
              :hotspot
              {:x 0.505,
               :y 0.49999999999999994,
               :height 0.8078078078078077,
               :width 0.5419999999999998}}]
            {:image-options {:w 320
                             :h 240}})
           "<div><p>Also, images are pretty common.</p><figure><img src=\"https://cdn.sanity.io/images/3do82whm/production/c2f0fc30003e6d7c79dcb5338a9b3d297cab4a8a-2000x1333.jpg?rect=468,128,1084,1077&amp;w=320&amp;h=240\"/></figure></div>")))

  (defn crop-options [w h {:keys [crop hotspot]} opt]
    (let [hotspot (merge {:x 0.5 :y 0.5 :height 1 :width 1} hotspot)
          [hs-c-x hs-c-y] [(* (:x hotspot) w) (* (:y hotspot) h)]
          [hs-w hs-h] [(* (:width hotspot) w) (* (:height hotspot) h)]]
      {:x1 (int (Math/round (* (:left crop) w)))
       ;;:x2 (- w (int (Math/round (* (:right crop) w))))
       :y1 (int (Math/round (* (:top crop) h)))
       :hotspot [[hs-c-x hs-c-y]
                 [hs-w hs-h]]}))

  (crop-options
   2000
   1333
   {:crop
    {:top 0.0960960960960961,
     :bottom 0.09609609609609615,
     :left 0.2340000000000001,
     :right 0.2240000000000001},
    :hotspot
    {:x 0.505,
     :y 0.49999999999999994,
     :height 0.8078078078078077,
     :width 0.5419999999999998}}
   {:w 320
    :h 240})

  ;; 468,128,1084,1077&amp;w=320&amp;h=240
)

(deftest inline-block-with-text-026
  (is (= (sut/render
          [{:_type "block",
            :_key "foo",
            :style "normal",
            :children
            [{:_type "span", :text "Men, "}
             {:_type "button", :text "bli med du også"}
             {:_type "span", :text ", da!"}]}])
         "<p>Men, <button>bli med du også</button>, da!</p>")))

(deftest styled-list-items-027
  (is (= (sut/render
          [{:style "normal",
            :_type "block",
            :_key "f94596b05b41",
            :markDefs [],
            :children
            [{:_type "span",
              :text "Let's test some of these lists!",
              :marks []}]}
           {:listItem "bullet",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "937effb1cd06",
            :markDefs [],
            :children [{:_type "span", :text "Bullet 1", :marks []}]}
           {:listItem "bullet",
            :style "h1",
            :level 1,
            :_type "block",
            :_key "bd2d22278b88",
            :markDefs [],
            :children [{:_type "span", :text "Bullet 2", :marks []}]}
           {:listItem "bullet",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "a97d32e9f747",
            :markDefs [],
            :children [{:_type "span", :text "Bullet 3", :marks []}]}])
         "<div><p>Let&#x27;s test some of these lists!</p><ul><li>Bullet 1</li><li><h1>Bullet 2</h1></li><li>Bullet 3</li></ul></div>")))

(deftest custom-block-type-050
  (is (= (sut/render
          [{:_type "code",
            :_key "9a15ea2ed8a2",
            :language "javascript",
            :code
            "const foo = require('foo')\n\nfoo('hi there', (err, thing) => {\n  console.log(err)\n})\n"}])
         "<pre data-language=\"javascript\"><code>const foo = require(&#x27;foo&#x27;)\n\nfoo(&#x27;hi there&#x27;, (err, thing) =&gt; {\n  console.log(err)\n})\n</code></pre>")))

(comment
  ;; JS lib overrides via functions passed as opts
  ;; We probably want to use multimethods instead
  (deftest override-defaults-051
    (is (= (sut/render
            [{:_type "image",
              :_key "d234a4fa317a",
              :asset
              {:_type "reference",
               :_ref "image-YiOKD0O6AdjKPaK24WtbOEv0-3456x2304-jpg"}}])
           "<img alt=\"Such image\" src=\"https://cdn.sanity.io/images/3do82whm/production/YiOKD0O6AdjKPaK24WtbOEv0-3456x2304.jpg\"/>"))))

(deftest custom-marks-052
  (is (= (sut/render
          {:_type "block",
           :children
           [{:_key "a1ph4", :_type "span", :marks ["mark1"], :text "Sanity"}],
           :markDefs [{:_key "mark1", :_type "highlight", :thickness 5}]})
         "<p><span style=\"border:5px solid;\">Sanity</span></p>")))

(comment
  ;; JS lib overrides via functions passed as opts
  ;; We probably want to use multimethods instead
  (deftest override-default-marks-053
    (is (= (sut/render
            {:_type "block",
             :children
             [{:_key "a1ph4", :_type "span", :marks ["mark1"], :text "Sanity"}],
             :markDefs [{:_key "mark1", :_type "link", :href "https://sanity.io"}]})
           "<p><a class=\"mahlink\" href=\"https://sanity.io\">Sanity</a></p>"))))

(deftest missing-mark-serializer-061
  (is (= (sut/render
          {:_type "block",
           :children
           [{:_key "cZUQGmh4", :_type "span", :marks ["abc"], :text "A word of "}
            {:_key "toaiCqIK",
             :_type "span",
             :marks ["abc" "em"],
             :text "warning;"}
            {:_key "gaZingA",
             :_type "span",
             :marks [],
             :text " Sanity is addictive."}],
           :markDefs []})
         "<p><span>A word of <em>warning;</em></span> Sanity is addictive.</p>")))

(deftest basic-bullet-list-010-lisp-cased-data
  (is (= (sut/render
          [{:style "normal",
            :_type "block",
            :_key "f94596b05b41",
            :mark-defs [],
            :children
            [{:_type "span",
              :text "Let's test some of these lists!",
              :marks []}]}
           {:list-item "bullet",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "937effb1cd06",
            :mark-defs [],
            :children [{:_type "span", :text "Bullet 1", :marks []}]}
           {:list-item "bullet",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "bd2d22278b88",
            :mark-defs [],
            :children [{:_type "span", :text "Bullet 2", :marks []}]}
           {:list-item "bullet",
            :style "normal",
            :level 1,
            :_type "block",
            :_key "a97d32e9f747",
            :mark-defs [],
            :children [{:_type "span", :text "Bullet 3", :marks []}]}])
         "<div><p>Let&#x27;s test some of these lists!</p><ul><li>Bullet 1</li><li>Bullet 2</li><li>Bullet 3</li></ul></div>")))

(defmethod sut/render-mark :internalLink [config mark content]
  [:a {:href (format "/files/%s" (-> mark :reference :_id))} content])

(deftest custom-mark-implementation
  (is (= (sut/render
          [{:_key "8cff0fcf4e1c"
            :_type "block"
            :children
            [{:_key "8cff0fcf4e1c1"
              :_type "span"
              :marks ["7d81b2a6f6b6"]
              :text "A document"}]
            :mark-defs
            [{:_key "7d81b2a6f6b6"
              :_type "internalLink"
              :reference {:_id "2336fbf3-5d55-4dad-a8d1-5b074a4d874a"
                          :_type "file"
                          :title "My file"}}]
            :style "normal"}])
         "<p><a href=\"/files/2336fbf3-5d55-4dad-a8d1-5b074a4d874a\">A document</a></p>")))

(deftest custom-mark-in-list
  (is (= (sut/render
          (list
           {:_key "0d14be59568c",
            :_type "block",
            :children
            [{:_key "0d14be59568c0",
              :_type "span",
              :marks [],
              :text "Meld flytting til kundeservice på telefon "}
             {:_key "0d14be59568c1",
              :_type "span",
              :marks ["440bd7a4f597"],
              :text "21 49 69 10"}
             {:_key "0d14be59568c2", :_type "span", :marks [], :text ""}],
            :level 1,
            :list-item "bullet",
            :mark-defs [{:_key "440bd7a4f597", :_type "telLink", :tel "21496910"}],
            :style "normal"}))
         "<ul><li>Meld flytting til kundeservice på telefon <span tel=\"21496910\">21 49 69 10</span></li></ul>")))

(defmethod sut/render-mark :terse [config mark content]
  [:a content])

(deftest custom-mark-with-terse-hiccup
  (is (= (sut/to-hiccup
          [{:_key "8cff0fcf4e1c"
            :_type "block"
            :children
            [{:_key "8cff0fcf4e1c1"
              :_type "span"
              :marks ["7d81b2a6f6b6"]
              :text "A document"}]
            :mark-defs
            [{:_key "7d81b2a6f6b6"
              :_type "terse"
              :reference {:_id "2336fbf3-5d55-4dad-a8d1-5b074a4d874a"
                          :_type "file"
                          :title "My file"}}]
            :style "normal"}])
         [:p {} [:a {} "A document"]])))

(defmethod sut/render-block :priceCalculatorPlaceholder [config block]
  [(keyword (str "span." (:class-name block)))])

(deftest custom-block-rendering
  (is (= (sut/to-hiccup
          [{:_key "2a421de718f1"
            :_type "block"
            :children
            [{:_key "2a421de718f10"
              :_type "span"
              :marks []
              :text "Strømkostnad du ville fått i "}
             {:_id "9a58f24f-d65c-469f-afce-33f2f4f9f22d"
              :_type "priceCalculatorPlaceholder"
              :class-name "js-el-price-month"
              :title "Gjeldende måned"}
             {:_key "2a421de718f12"
              :_type "span"
              :marks []
              :text "."}]
            :mark-defs []
            :style "normal"}])
         [:p {}
          "Strømkostnad du ville fått i "
          [:span.js-el-price-month]
          "."])))

(deftest custom-block-rendering
  (is (= (sut/to-html
          [:p {}
           '("Strømkostnad du ville fått i ")
           [:span.js-el-price-month [:span.inner]]
           '(".")])
         "<p>Strømkostnad du ville fått i <span class=\"js-el-price-month\"><span class=\"inner\"></span></span>.</p>")))

(defmethod sut/render-block :productPlaceholder [config block]
  [:span (format "{{%s}}" (:var-name block))])

(deftest produces-readable-hiccup-without-lots-of-nested-seqs
  (is (= (sut/to-hiccup
          {:_key "73335d2fc3be"
           :_type "block"
           :children
           [{:_type "span"
             :marks []
             :text "Some text here"}
            {:_type "span"
             :marks []
             :text
             ", and then more text over here. "}
            {:_type "span"
             :marks []
             :text
             "And here:\na newline"}]
           :mark-defs []
           :style "normal"})
         [:p {} "Some text here, and then more text over here. And here:" [:br] "a newline"])))

(deftest is-capable-of-converting-lean-hiccup-to-html
  (is (= (sut/to-html [:span.ml-text "Broom"])
         "<span class=\"ml-text\">Broom</span>")))
