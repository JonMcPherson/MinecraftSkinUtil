# MinecraftSkinUtil

A lightweight and simple utility that allows you to create images of different parts and positions from a Minecraft player's current skin texture.  Useful for creating player skin previews, avatars, and profile pictures.

Basic useful feature list:

* Works with all skin types: 64x32, 64x64, slim, and legacy
* Applies skin "jacket" overlay where applicable while maintaining opacity
* Uses appropriate default skin ("Steve" or "Alex") when the player has no skin
* Convenient conversion of image to byte array and data URI
* Easily extensible to create different positions from skin parts

![Example][honeydew]


### Requirements:

This utility must be used with my fork of the Mojang [AccountsClient](https://github.com/JonMcPherson/AccountsClient) library.
This dependency is automatically included when compiling with Maven since it is defined in the POM definition and obtained from my Maven repository at https://deadmandungeons.com/artifactory/public

Using AccountsClient, you can get current profile information on a player through the Mojang API. Either retrieve a player's Profile by username which includes their unique ID, or retrieve a player's MinecraftProfile by ID which also includes their skin and cape texture information.

All methods of MinecraftSkinUtil accept the MinecraftProfile for the player who's skin should be retrieved since it contains the texture information necessary to construct the images.


### Compiling:

Using Maven, simply add the following repository and dependency to your project POM definition:
```xml
<repository>
	<id>deadman-dungeons</id>
	<url>https://deadmandungeons.com/artifactory/public</url>
</repository>
```
```xml
<dependency>
    <groupId>com.deadmandungeons</groupId>
    <artifactId>mc-skin-util</artifactId>
    <version>1.0.0</version>
</dependency>
```


### Example Usage:

Get an image of a player's face that is 64x64 in size (or 8 times larger than original 1x1 pixel scale since player faces are 8x8 pixels):
```java
SkinImage face = MinecraftSkinUtil.getPlayerSkinPart(profile, SkinPart.HEAD_FRONT, 8);
```

Get an image of a player's front position:
```java
SkinImage front = MinecraftSkinUtil.getPlayerSkinFront(profile, 8)
```

Convert SkinImage result to other useful formats:
```java
BufferedImage image = front.getImage();
byte[] imageData = front.toByteArray();
String imageDataUri = front.toDataUri();
```

These examples in practice:
```java
UUID id = UUID.fromString("fc346643-0f0c-4830-8534-c50dac0b331d");
MinecraftProfileRepository repository = new HttpProfileRepository();
MinecraftProfile profile = repository.findProfileById(id);

SkinImage face = MinecraftSkinUtil.getPlayerSkinPart(profile, SkinPart.HEAD_FRONT, 8);
String faceHtml = "<img src=\"" + face.toDataUri() + "\" />";

SkinImage front = MinecraftSkinUtil.getPlayerSkinFront(profile, 8);
String frontHtml = "<img src=\"" + front.toDataUri() + "\" />";
```


##### Face Result:

![Skin Face][face_result]

##### Front Result:

![Skin Front][front_result]


### Planed Features:

* Include player cape as a SkinPart
* Option to include player cape in skin positions
* Option to show player cape as when Elytra is equipped


[honeydew]: data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIAAAAEACAYAAAB7+X6nAAAEIUlEQVR42u3doW5UQRQG4H2HDUEAtknFihKCICyygq3DYsEgIME1fQ0Iuk/QNyDpIyB4izoE7tYh54Scnc7snu8kv7y9d+d8NSd35q5Wg+vyy8elclbVCwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgKOsN69eLq2cbU4POtHvAwAAAAAAAAAAAAAAAAAAAAAAAOoEAAAAAAAAAAAAAAAAAAAAAAAq1a9vl0sru92umej63sk+HwAAAAAAAAAAAAAAAAAAAAAAVKo/P6+XVtbrdTN37x8NTfR80e8DAAAAAAAAAAAAAAAAAAAAAIBKdff28dJM0IBogaNBTZTo74dIgt8HAAAAAAAAAAAAAAAAAAAAAAClACQXMLr+7+enzaSvTz4fAAAAAAAAAAAAAAAAAAAAAAA8ZH399GHJ5Hz7IpXZG5wF8G570kx2/bL9AwAAAAAAAAAAAAAAAAAAAACA/2lw7wMYskDSie6fBJBdnywQAAAAAAAAAAAAAAAAAAAAAGoByDY42jiRTXT/7CAoOyhabp81s908aab3+kT9BQAAAAAAAAAAAAAAAAAAAKAWgNENziY9KAqujxp8tjltJrp+9D8QAAAAAAAAAAAAAAAAAAAAALUAzN7g3z+umskCiV7YuNhumokaPPs/GAAAAAAAAAAAAAAAAAAAAAAAHFOiBkeJXug49EEaAAAAAAAAAAAAAAAAAAAAALUAZA8qzKZ3A2dv8Oj1BwAAAAAAAAAAAAAAAAAAAKAWgOwHB7Lp3cDZBzmj1x8AAAAAAAAAAAAAAAAAAACAWgBGDyJ6N3D2FzbKTwIBAAAAAAAAAAAAAAAAAAAAmApAdBBiNr0bFB0wkT2AIpve6wsAAAAAAAAAAAAAAAAAAAAAAA85CIoeMP3BiOCDEL2v7w3g4CeBAAAAAAAAAAAAAAAAAAAAAEwFINo40HtQ1P3DkpMD6T3oSe8MAgAAAAAAAAAAAAAAAAAAAOCoAIyu7CDj5vr70KQHMdULAAAAAAAAAAAAAAAAAAAAgFLVe2PE7AEAAAAAAAAAAAAAAAAAAAAAAAD2tzGi9wsX2fsDAAAAAAAAAAAAAAAAAAAAAEClym5sCDe2vH6eSvr+XggBAAAAAAAAAAAAAAAAAAAAAIB/FS1gduNGtoG97w8AAAAAAAAAAAAAAAAAAAAAAADUCQAAAAAAAAAAAAAAAAAAAAAAVKruL4QkN3Z4IQQAAAAAAAAAAAAAAAAAAAAAAPZXDogAAAAAAAAAAAAAAAAAAAAAAACDoCoBAAAAAAAAAAAAAAAAAAAAAIBKlT2A4dADAAAAAAAAAAAAAAAAAAAAAACVygERAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAALD3ugfj4OL1y90tUgAAAABJRU5ErkJggg==

[face_result]: data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAABJ0lEQVR42u3ZsUrDUBSH8TyAZszQDBbBSRBxaBEC7SwZ1NIOFW21Y5uxQyFPkNVJB59CBzdBcOhQyN4hLyD4BqlP8D/DJVC4X+Dbwj33/NYbJMeHtUtZP5adxweyYnQiu+9GMut+1vwAAAAAAAAAADwGsBawBuRpWza9jGRfs57MFcAKAAAAAAAAAHwGePg/RPWZXcgsgJ+7K9n32a2sepnLrPtZAQAAAAAAAIDPANYPb0+nTpXpRLZNlrJOK3Sqel3IAAAAAAAAAMBnANcFN9djmbXg33sh+6iPZNZnAQEAAAAAAACAzwDWw8R6OJC5LmiVP4eyVRnIrPMBAAAAAAAAwGeAphfY9wAAAAAAAADAZwDr4cB1wO/jjVMAAAAAAAAAAEBj7QC+KhC7dUHuxQAAAABJRU5ErkJggg==

[front_result]: data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIAAAAEACAYAAAB7+X6nAAAFNklEQVR42u3dzYtXVRjA8buLRmcsJTMlJgQhMEoSZohGDCKMMBknalHUWC5LWkwtAjdtWsxmYALxpfwbWtRCa7BoMDAIXURYQrNx0aIXkdq0uLl09TzEw+1ncz4Hvsv7ds5ndbi/3+26EY+ZneN9pWNP7Qjbs2Nj2OJLu8Jend4alt1fdv2u9QEAAAAAAAAAAAAAAAAAAABAUyNbwGyCjx98KOzIE1vDvjy6P6wKIAsAAAAAAAAAAAAAAAAAAAAAAFoar92axKhzxx4PywBcfOW5sK8fnQtbO/VmWHZ/WQAAAAAAAAAAAAAAAAAAAAAA0NLIJujsG7tLXTk4H3Zt5t2wqe0TpdZOvxUGAAAAAAAAAAAAAAAAAAAAAAAtjeoCfzf7cli2wH98uhj2WT8Zlo0MCAAAAAAAAAAAAAAAAAAAAAAAtDSyH2ZcevGFsOoCZx3/cCLsvStdWHZ+AAAAAAAAAAAAAAAAAAAAAABoaQy9gHd6AAAAAAAAAAAAAAAAAAAAAADQ0sh+OFGd4F9fP1wKAAAAAAAAAAAAAAAAAAAAAADWE4DqA1xYXiiV/XDit+u/hGWAqsCy47P7y56vOn9lgAAAAAAAAAAAAAAAAAAAAAAAsI4AVJ9vbXEuDAAAAAAAAAAAAAAAAAAAAAAAWgJQBZJN0JOTm8KyBcj6+Ycfw746vxK254GNYXu3jYcNvcAAAAAAAAAAAAAAAAAAAAAAAHA7gOoLFdlGSfaA0w9OhD1y34awI1ueCftiZSXszJmPwv7uurD+wIGwx24hiMrmJ5vf6voBAAAAAAAAAAAAAAAAAAAA0BaA/sSJfpTt2jwW1p+8Hnf5WtzUzbjk+NX7N4RlQA7dOxU26vkHAAAAAAAAAAAAAAAAAAAAoC0A1Rcqqt1YvRxWXcC0gYH9ufxT2KjnHwAAAAAAAAAAAAAAAAAAAIC2AIx6I+LGxdmwm98cDqsucHb+6vXT41vfCQQAAAAAAAAAAAAAAAAAAACgqfcBdt5zd9jHCzNh1QU8+85Mqd9XZ8Oy52t+JxAAAAAAAAAAAAAAAAAAAACApt4HeHjLWFi20dJ1XdgnHzxdqnr99Pla3wkEAAAAAAAAAAAAAAAAAAAAoKn3AbIJyiZ4aADV62fP1/xOIAAAAAAAAAAAAAAAAAAAAAB3FIBsIyH7MGH2wYLsgxDVBfx+376wfn4+rHr97Pmy+cnmN1sfAAAAAAAAAAAAAAAAAAAAAAD4N78LqN5AdvzebeNh2QRmGy1Dl91f9nxDz2/5jSAAAAAAAAAAAAAAAAAAAAAA1hWAUY+xt2f7qKntE6Xuen66VPX62fN1rQ8AAAAAAAAAAAAAAAAAAAAAmhqbv13uo7IJrB4/9Pmz4wEAAAAAAAAAAAAAAAAAAAAAoKVx8q+rfVQ2gdXjhz5/djwAAAAAAAAAAAAAAAAAAAAAALQ0shcyso2W6vFDnz87HgAAAAAAAAAAAAAAAAAAAAAAWhoXlhf6SmuLcyOtev8AAAAAAAAAAAAAAAAAAAAAANDSyP7osFr2QYbqBxuqAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADA/2Fkf8Bw6f1Dpap/ADH09QEAAAAAAAAAAAAAAAAAAAAAoKWxtLTUR537/Hyp7Pyjvj4AAAAAAAAAAAAAAAAAAAAAALQ0nt092bccAAAAAAAAAAAAAAAAAAAAAAD8h+Mfcmo5dyQ99RAAAAAASUVORK5CYII=