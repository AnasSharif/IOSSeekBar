
## Download
 - Use Gradle
```
implementation 'com.xdevelopers.iosseekbar:IOSSeekbar:1.0.0'
```
- or Maven

```
<dependency>
  <groupId>com.xdevelopers.iosseekbar</groupId>
  <artifactId>IOSSeekbar</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

## Usage

 - XML Decleration 
 ```
  <com.xdevelopers.iosseekbar.IOSColorSeekBar
        android:id="@+id/iosColorSlider"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="20dp"
        android:layout_centerInParent="true"
        app:shadowLayer="true"/>
```

- Attributes

<table>
 <th>Seekbar Attribute</th>
 <th>Description</th>
  <tr>
    <td>app:cornerRadius="4dp"</td>
    <td>Sets the corner radius of bar.</td>
 </tr> 
 <tr>
    <td>app:barHeight="8dp</td>
    <td>Sets the height of color bar.</td>
 </tr>
  <tr>
    <td>app:shadowLayer="true"</td>
    <td>Sets the thumb shadow.</td>
 </tr>
  <tr>
    <td>app:shadowLayerRadius="10"</td>
    <td>Set the thumb shadow layer radius.</td>
 </tr>
 <tr>
    <td>app:shadowLayerColor="#e0e0e0"</td>
    <td>Set the thumb shadow layer color.</td>
 </tr>
 </table>
 
 - Color brightness 
  ```
  colorSlider.setColorBrightness(0.8f)
  ```
 
 - Listener
 
 ```
        colorSlider.setOnColorChangeListener(object : IOSColorSeekBar.OnColorChangeListener {
            override fun onColorChangeListener(color: Int) {
                colorView.setBackgroundColor(color)
            }

            override fun onThumbClickedListener(color: Int) {
                colorView.setBackgroundColor(color)
                Toast.makeText(this@MainActivity, "Thumb clicked", Toast.LENGTH_SHORT).show()
            }
        })
```
## Connect

- [Medium](https://medium.com/@divyanshub024)

## LICENCE
```
Copyright 2020 xDevelopers.co

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
