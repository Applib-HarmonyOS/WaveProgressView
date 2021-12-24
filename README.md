[![Build](https://github.com/applibgroup/WaveProgressView/actions/workflows/main.yml/badge.svg)](https://github.com/applibgroup/WaveProgressView/actions/workflows/main.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=applibgroup_WaveProgressView&metric=alert_status)](https://sonarcloud.io/dashboard?id=applibgroup_WaveProgressView)

# WaveProgressView
WaveProgressView

a simple progress view.

<img src="/image/wave_progress_view.gif" alt="progress-circular-indeterminate" title="progress-circular-indeterminate" width="477" height="791" />

# Source
This library has been inspired by [zeng1990java/WaveProgressView](https://github.com/zeng1990java/WaveProgressView).

## Integration

1. For using WaveProgressView module in sample app, include the source code and add the below dependencies in entry/build.gradle to generate hap/support.har.
```
 implementation project(path: ':library')
```
2. For using WaveProgressView module in separate application using har file, add the har file in the entry/libs folder and add the dependencies in entry/build.gradle file.
```
 implementation fileTree(dir: 'libs', include: ['*.har'])
```
3. For using WaveProgressView module from a remote repository in separate application, add the below dependencies in entry/build.gradle file.
```
implementation 'dev.applibgroup:WaveProgressView:1.0.0'
```

###usage
```xml
<com.github.zeng1990java.widget.WaveProgressView
        ohos:id="$+id:waveView3"
        ohos:height="200vp"
        ohos:width="200vp"
        ohos:center_in_parent="true"
        custom:animatorEnable="true"
        custom:textHidden="true"
        custom:frontColor="#FF69B4"
        custom:behideColor="#FFC0CB"
        custom:borderColor="#FF69B4"
        custom:borderWidthSize="7vp"
        custom:max="1000"
        custom:progress="0"
        custom:strong="80"
        />
```

License
============

The MIT License (MIT)

Copyright (c) 2015 zeng1990java

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
