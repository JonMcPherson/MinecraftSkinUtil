# MinecraftSkinUtil

A lightweight and simple utility that allows you to create images of different parts and positions from a Minecraft player's current skin texture.  Useful for creating player skin previews, avatars, and profile pictures.

Basic useful feature list:

* Works with all skin types: 64x32, 64x64, slim, and legacy
* Applies skin "jacket" overlay where applicable while maintaining opacity
* Uses appropriate default skin ("Steve" or "Alex") when the player has no skin
* Convenient conversion of image to byte array and data URI
* Easily extensible to create different positions from skin parts

![Example](https://cloud.githubusercontent.com/assets/9062811/22094416/3af1f9d8-dddb-11e6-9e1a-891f78f349e9.png)


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

![Face Result](https://cloud.githubusercontent.com/assets/9062811/22094418/3af4a2e6-dddb-11e6-9846-5a330f697ed1.png)

##### Front Result:

![Front Result](https://cloud.githubusercontent.com/assets/9062811/22094417/3af29316-dddb-11e6-953f-8fd5d6102513.png)


### Planed Features:

* Include player cape as a SkinPart
* Option to include player cape in skin positions
* Option to show player cape as when Elytra is equipped
