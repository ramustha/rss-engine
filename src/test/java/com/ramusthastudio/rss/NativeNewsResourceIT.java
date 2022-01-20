package com.ramusthastudio.rss;

import com.ramusthastudio.rss.resources.NewsResourceTest;
import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeNewsResourceIT extends NewsResourceTest {

    // Execute the same tests but in native mode.
}