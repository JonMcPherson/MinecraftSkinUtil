package com.deadmandungeons.skinutil;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.mojang.api.profiles.minecraft.MinecraftProfile;
import com.mojang.api.profiles.minecraft.MinecraftProfile.Skin;


public class MinecraftSkinUtil {
	
	private static final Logger LOGGER = Logger.getLogger(MinecraftSkinUtil.class.getName());
	private static final SkinTexture STEVE_SKIN;
	private static final SkinTexture ALEX_SKIN;
	static {
		try {
			Class<?> self = MinecraftSkinUtil.class;
			STEVE_SKIN = new SkinTexture(ImageIO.read(self.getResource("/default-skins/steve.png")), false, true);
			ALEX_SKIN = new SkinTexture(ImageIO.read(self.getResource("/default-skins/alex.png")), true, true);
		} catch (IOException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	
	/**
	 * If the player does not have a skin or if the skin image could not be read,
	 * the correct default skin for the player will be returned ("Steve" or "Alex").
	 * @param profile - The Minecraft profile of the player who's skin should be retrieved
	 * @return a SkinTexture of the player's skin at 1x1 scale
	 */
	public static SkinTexture getPlayerSkin(MinecraftProfile profile) {
		Optional<Skin> skin = profile.getTextures().getSkin();
		if (skin.isPresent()) {
			try {
				BufferedImage skinImage = ImageIO.read(new URL(skin.get().getUrl()));
				if (skinImage != null) {
					return new SkinTexture(skinImage, skin.get().isSlimModel(), false);
				}
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Failed to read skin image for " + profile, e);
			}
		}
		// The hashcode of the player's UUID determines which defualt skin to use.
		// Steve is used when the hashcode is even, and Alex (slim) is used when it is odd.
		return (profile.getId().hashCode() & 1) == 0 ? STEVE_SKIN : ALEX_SKIN;
	}
	
	/**
	 * @param profile - The Minecraft profile of the player who's skin should be retrieved
	 * @return a SkinImage of the player's currently equipped cape at 1x1 scale,
	 * or <code>null</code> if the player has no cape or if the cape image could not be read
	 */
	public static SkinImage getPlayerCape(MinecraftProfile profile) {
		Optional<String> capeUrl = profile.getTextures().getCapeUrl();
		if (capeUrl.isPresent()) {
			try {
				BufferedImage capeImage = ImageIO.read(new URL(capeUrl.get()));
				if (capeImage != null) {
					return new SkinImage(capeImage);
				}
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Failed to read cape image for " + profile, e);
			}
		}
		return null;
	}
	
	
	/**
	 * @param profile - The Minecraft profile of the player who's skin should be retrieved
	 * @param size - The size multiplier of a single pixel in the returned image
	 * @return a SkinImage showing the front side of the player's currently equipped skin at <code>size</code> scale
	 * @throws IllegalArgumentException if the given size is less than 1 or greater than 50
	 */
	public static SkinImage getPlayerSkinFront(MinecraftProfile profile, int size) throws IllegalArgumentException {
		return getPlayerSkinPosition(profile, DefaultSkinPosition.FRONT, size);
	}
	
	/**
	 * @param profile - The Minecraft profile of the player who's skin should be retrieved
	 * @param size - The size multiplier of a single pixel in the returned image
	 * @return a SkinImage showing the back side of the player's currently equipped skin at <code>size</code> scale
	 * @throws IllegalArgumentException if the given size is less than 1 or greater than 50
	 */
	public static SkinImage getPlayerSkinBack(MinecraftProfile profile, int size) throws IllegalArgumentException {
		return getPlayerSkinPosition(profile, DefaultSkinPosition.BACK, size);
	}
	
	/**
	 * @param profile - The Minecraft profile of the player who's skin should be retrieved
	 * @param size - The size multiplier of a single pixel in the returned image
	 * @return a SkinImage showing the left side of the player's currently equipped skin at <code>size</code> scale
	 * @throws IllegalArgumentException if the given size is less than 1 or greater than 50
	 */
	public static SkinImage getPlayerSkinLeft(MinecraftProfile profile, int size) throws IllegalArgumentException {
		return getPlayerSkinPosition(profile, DefaultSkinPosition.LEFT, size);
	}
	
	/**
	 * @param profile - The Minecraft profile of the player who's skin should be retrieved
	 * @param size - The size multiplier of a single pixel in the returned image
	 * @return a SkinImage showing the right side of the player's currently equipped skin at <code>size</code> scale
	 * @throws IllegalArgumentException if the given size is less than 1 or greater than 50
	 */
	public static SkinImage getPlayerSkinRight(MinecraftProfile profile, int size) throws IllegalArgumentException {
		return getPlayerSkinPosition(profile, DefaultSkinPosition.RIGHT, size);
	}
	
	/**
	 * @param profile - The Minecraft profile of the player who's skin should be retrieved
	 * @param size - The size multiplier of a single pixel in the returned image
	 * @return a SkinImage showing the top side of the player's currently equipped skin at <code>size</code> scale
	 * @throws IllegalArgumentException if the given size is less than 1 or greater than 50
	 */
	public static SkinImage getPlayerSkinTop(MinecraftProfile profile, int size) throws IllegalArgumentException {
		return getPlayerSkinPosition(profile, DefaultSkinPosition.TOP, size);
	}
	
	/**
	 * @param profile - The Minecraft profile of the player who's skin should be retrieved
	 * @param size - The size multiplier of a single pixel in the returned image
	 * @return a SkinImage showing the bottom side of the player's currently equipped skin at <code>size</code> scale
	 * @throws IllegalArgumentException if the given size is less than 1 or greater than 50
	 */
	public static SkinImage getPlayerSkinBottom(MinecraftProfile profile, int size) throws IllegalArgumentException {
		return getPlayerSkinPosition(profile, DefaultSkinPosition.BOTTOM, size);
	}
	
	/**
	 * @param profile - The Minecraft profile of the player who's skin should be retrieved
	 * @param position - The SkinPosition describing the orientation of SkinPart's to be combined in the returned SkinImage
	 * @param size - The size multiplier of a single pixel in the returned image
	 * @return a SkinImage showing the given position of the player's currently equipped skin at <code>size</code> scale
	 * @throws IllegalArgumentException if the given size is less than 1 or greater than 50
	 */
	public static SkinImage getPlayerSkinPosition(MinecraftProfile profile, SkinPosition position, int size) throws IllegalArgumentException {
		validateSize(size);
		
		SkinTexture skin = getPlayerSkin(profile);
		
		int width = position.getImageWitdh(skin.slimSkin) * size;
		int height = position.getImageHeight(skin.slimSkin) * size;
		BufferedImage positionImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = positionImage.createGraphics();
		
		for (PartPosition partPosition : position.getPartPositions()) {
			BufferedImage partImage = getSkinPart(skin, partPosition.part, size);
			int offsetX = partPosition.getOffsetX(skin.slimSkin);
			int offsetY = partPosition.getOffsetY(skin.slimSkin);
			graphics.drawImage(partImage, offsetX * size, offsetY * size, null);
		}
		return new SkinImage(positionImage);
	}
	
	
	/**
	 * Equivalent to {@link #getPlayerSkinPart(MinecraftProfile, SkinPart, int) getPlayerSkinPart(profile, part, 1)}
	 * @param profile - The Minecraft profile of the player who's skin should be retrieved
	 * @param part - The desired SkinPart to be shown in the returned SkinImage
	 * @return a SkinImage showing the given part of the player's currently equipped skin at 1x1 scale
	 * @throws IllegalArgumentException if the given size is less than 1 or greater than 50
	 */
	public static SkinImage getPlayerSkinPart(MinecraftProfile profile, SkinPart part) throws IllegalArgumentException {
		return getPlayerSkinPart(profile, part, 1);
	}
	
	/**
	 * @param profile - The Minecraft profile of the player who's skin should be retrieved
	 * @param part - The desired SkinPart to be shown in the returned SkinImage
	 * @param size - The size multiplier of a single pixel in the returned image
	 * @return a SkinImage showing the given part of the player's currently equipped skin at <code>size</code> scale
	 * @throws IllegalArgumentException if the given size is less than 1 or greater than 50
	 */
	public static SkinImage getPlayerSkinPart(MinecraftProfile profile, SkinPart part, int size) throws IllegalArgumentException {
		validateSize(size);
		
		return new SkinImage(getSkinPart(getPlayerSkin(profile), part, size));
	}
	
	private static BufferedImage getSkinPart(SkinTexture skin, SkinPart part, int size) throws IllegalArgumentException {
		BufferedImage image = skin.getImage();
		
		ImageArea partArea = part.area;
		ImageArea overlayArea = part.overlayArea;
		
		// Is the given part not available for small skins
		boolean useSmallSkinPart = !skin.largeSkin && part.smallSkinPart != null;
		if (useSmallSkinPart) {
			partArea = part.smallSkinPart.area;
			overlayArea = part.smallSkinPart.overlayArea;
		} else if (skin.slimSkin && part.slimSkinPart != null) {
			partArea = part.slimSkinPart.area;
			overlayArea = part.slimSkinPart.overlayArea;
		}
		
		// Get skin part image from texture image as the base layer and set non-opaque pixels to black
		BufferedImage partImage = image.getSubimage(partArea.x, partArea.y, partArea.w, partArea.h);
		for (int x = 0; x < partImage.getWidth(); ++x) {
			for (int y = 0; y < partImage.getHeight(); ++y) {
				int pixel = partImage.getRGB(x, y);
				if (!isOpaque(pixel)) {
					partImage.setRGB(x, y, 0xFF000000);
				}
			}
		}
		
		// Draw skin part overlay layer if applicable
		if (skin.hasOverlay(overlayArea)) {
			BufferedImage overlayImage = image.getSubimage(overlayArea.x, overlayArea.y, overlayArea.w, overlayArea.h);
			
			Graphics graphics = partImage.getGraphics();
			graphics.drawImage(overlayImage, 0, 0, null);
			graphics.dispose();
		}
		
		// Small skins have the right arm and leg parts flipped for the left side
		if (useSmallSkinPart) {
			partImage = flipImage(partImage);
		}
		
		// Finally resize the image if needed
		if (size > 1) {
			BufferedImage enlargedPartImage = new BufferedImage(size * partArea.w, size * partArea.h, image.getType());
			for (int x = 0; x < partArea.w; ++x) {
				for (int y = 0; y < partArea.h; ++y) {
					int pixel = partImage.getRGB(x, y);
					drawSquare(enlargedPartImage, x * size, y * size, size, pixel);
				}
			}
			partImage = enlargedPartImage;
		}
		
		return partImage;
	}
	
	
	private static BufferedImage flipImage(BufferedImage image) {
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-(image.getWidth(null)), 0);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		return op.filter(image, null);
	}
	
	private static void drawSquare(BufferedImage image, int x, int y, int size, int pixel) {
		for (int px = 0; px < size; ++px) {
			for (int py = 0; py < size; ++py) {
				image.setRGB(x + px, y + py, pixel);
			}
		}
	}
	
	private static boolean isOpaque(int pixel) {
		return ((pixel >> 24) & 0xFF) == 0xFF;
	}
	
	private static void validateSize(double size) {
		if (size < 1 || size > 50) {
			throw new IllegalArgumentException("size cannot be less than 1 or greater than 50");
		}
	}
	
	
	/**
	 * A simple wrapper to a player skin PNG {@link BufferedImage} that includes helper methods
	 * for converting the image to byte array or data URI.
	 * @author Jon
	 */
	public static class SkinImage {
		
		private final BufferedImage image;
		
		private SkinImage(BufferedImage image) {
			this.image = image;
		}
		
		/**
		 * @return the original BufferedImage
		 */
		public BufferedImage getImage() {
			return image;
		}
		
		/**
		 * @return a new byte array of the data in this SkinImage
		 */
		public byte[] toByteArray() {
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				ImageIO.write(image, "png", baos);
				return baos.toByteArray();
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, "An unexpected error occured while writing skin image data to byte array", e);
				return new byte[0];
			}
		}
		
		/**
		 * @return a data URI for this SkinImage encoded in Base64 String
		 */
		public String toDataUri() {
			return "data:image/png;base64," + Base64.getEncoder().encodeToString(toByteArray());
		}
		
	}
	
	/**
	 * A subclass of SkinImage that represents an original skin texture image with additional information.
	 * @see SkinImage
	 * @author Jon
	 */
	public static class SkinTexture extends SkinImage {
		
		private static final ImageArea[] RIGHT_DEAD_AREAS = { new ImageArea(32, 0, 8, 8), new ImageArea(56, 0, 8, 8), new ImageArea(36, 16, 8, 4),
				new ImageArea(52, 16, 12, 4), new ImageArea(56, 20, 8, 12) };
		
		private final boolean slimSkin;
		private final boolean defaultSkin;
		
		private final boolean largeSkin;
		private Boolean hasOverlay;
		
		private SkinTexture(BufferedImage image, boolean slimSkin, boolean defaultSkin) {
			super(image);
			if (image.getWidth() != 64 || (image.getHeight() != 32 && image.getHeight() != 64)) {
				throw new IllegalArgumentException("invalid image dimensions");
			}
			
			this.slimSkin = slimSkin;
			this.defaultSkin = defaultSkin;
			
			largeSkin = image.getHeight() == 64;
		}
		
		/**
		 * @return <code>true</code> if this skin texture is for the slim skin model type, and <code>false</code> otherwise.
		 */
		public boolean isSlimSkin() {
			return slimSkin;
		}
		
		/**
		 * When <code>true</code> and {@link #isSlimSkin()} is <code>true</code>, this is the default Minecraft "Alex" skin.<br>
		 * When <code>true</code> and {@link #isSlimSkin()} is <code>false</code>, this is the default Minecraft "Steve" skin.
		 * @return <code>true</code> if this skin texture is a default Minecraft skin type, and <code>false</code> otherwise (custom skin).
		 */
		public boolean isDefaultSkin() {
			return defaultSkin;
		}
		
		private boolean hasOverlay(ImageArea overlayArea) {
			if (hasOverlay == null) {
				// In small 64x32 skins, if all of the "dead areas" on the right 32x32 half are opaque, the head overlay will not be used
				hasOverlay = largeSkin || !isDeadAreaOpaque(getImage());
			}
			return hasOverlay && (overlayArea.y < 32 || largeSkin);
		}
		
		private static boolean isDeadAreaOpaque(BufferedImage skinImage) {
			for (ImageArea deadArea : RIGHT_DEAD_AREAS) {
				BufferedImage deadAreaImage = skinImage.getSubimage(deadArea.x, deadArea.y, deadArea.w, deadArea.h);
				for (int x = 0; x < deadArea.w; ++x) {
					for (int y = 0; y < deadArea.h; ++y) {
						int pixel = deadAreaImage.getRGB(x, y);
						if (!isOpaque(pixel)) {
							return false;
						}
					}
				}
			}
			return true;
		}
		
	}
	
	
	/**
	 * An enum containing all Minecraft skin texture parts to be used with
	 * {@link MinecraftSkinUtil#getPlayerSkinPart(MinecraftProfile, SkinPart, int)}
	 * @author Jon
	 */
	public static enum SkinPart {
		HEAD_TOP(8, 0, 8, 8),
		HEAD_BOTTOM(16, 0, 8, 8),
		HEAD_RIGHT(0, 8, 8, 8),
		HEAD_FRONT(8, 8, 8, 8),
		HEAD_LEFT(16, 8, 8, 8),
		HEAD_BACK(24, 8, 8, 8),
		LEG_RIGHT_TOP(4, 16, 4, 4),
		LEG_RIGHT_BOTTOM(8, 16, 4, 4),
		LEG_RIGHT_OUTSIDE(0, 20, 4, 12),
		LEG_RIGHT_FRONT(4, 20, 4, 12),
		LEG_RIGHT_INSIDE(8, 20, 4, 12),
		LEG_RIGHT_BACK(12, 20, 4, 12),
		BODY_TOP(20, 16, 8, 4),
		BODY_BOTTOM(28, 16, 8, 4),
		BODY_RIGHT(16, 20, 4, 12),
		BODY_FRONT(20, 20, 8, 12),
		BODY_LEFT(28, 20, 4, 12),
		BODY_BACK(32, 20, 8, 12),
		ARM_RIGHT_TOP(44, 16, 4, 4, SlimSkinPart.ARM_RIGHT_TOP),
		ARM_RIGHT_BOTTOM(48, 16, 4, 4, SlimSkinPart.ARM_RIGHT_BOTTOM),
		ARM_RIGHT_OUTSIDE(40, 20, 4, 12),
		ARM_RIGHT_FRONT(44, 20, 4, 12, SlimSkinPart.ARM_RIGHT_FRONT),
		ARM_RIGHT_INSIDE(48, 20, 4, 12),
		ARM_RIGHT_BACK(52, 20, 4, 12, SlimSkinPart.ARM_RIGHT_BACK),
		// below: skin parts available in MC 1.8 for large 64x64 skins
		LEG_LEFT_TOP(20, 48, 4, 4, LEG_RIGHT_TOP),
		LEG_LEFT_BOTTOM(24, 48, 4, 4, LEG_RIGHT_BOTTOM),
		LEG_LEFT_OUTSIDE(16, 52, 4, 12, LEG_RIGHT_OUTSIDE),
		LEG_LEFT_FRONT(20, 52, 4, 12, LEG_RIGHT_FRONT),
		LEG_LEFT_INSIDE(24, 52, 4, 12, LEG_RIGHT_INSIDE),
		LEG_LEFT_BACK(28, 52, 4, 12, LEG_RIGHT_BACK),
		ARM_LEFT_TOP(36, 48, 4, 4, ARM_RIGHT_TOP, SlimSkinPart.ARM_LEFT_TOP),
		ARM_LEFT_BOTTOM(40, 48, 4, 4, ARM_RIGHT_BOTTOM, SlimSkinPart.ARM_LEFT_BOTTOM),
		ARM_LEFT_OUTSIDE(32, 52, 4, 12, ARM_RIGHT_OUTSIDE),
		ARM_LEFT_FRONT(36, 52, 4, 12, ARM_RIGHT_FRONT, SlimSkinPart.ARM_LEFT_FRONT),
		ARM_LEFT_INSIDE(40, 52, 4, 12, ARM_RIGHT_INSIDE),
		ARM_LEFT_BACK(44, 52, 4, 12, ARM_RIGHT_BACK, SlimSkinPart.ARM_LEFT_BACK);
		
		private final ImageArea area;
		private final ImageArea overlayArea;
		private final SkinPart smallSkinPart;
		private final SlimSkinPart slimSkinPart;
		
		private SkinPart(int x, int y, int w, int h) {
			this(x, y, w, h, null, null);
		}
		
		private SkinPart(int x, int y, int w, int h, SkinPart smallSkinPart) {
			this(x, y, w, h, smallSkinPart, null);
		}
		
		private SkinPart(int x, int y, int w, int h, SlimSkinPart slimSkinPart) {
			this(x, y, w, h, null, slimSkinPart);
		}
		
		private SkinPart(int x, int y, int w, int h, SkinPart smallSkinPart, SlimSkinPart slimSkinPart) {
			area = new ImageArea(x, y, w, h);
			SkinPartOverlay overlay = SkinPartOverlay.valueOf(name());
			overlayArea = new ImageArea(overlay.x, overlay.y, w, h);
			this.smallSkinPart = smallSkinPart;
			this.slimSkinPart = slimSkinPart;
			
			boolean largeSkinsOnly = y >= 32;
			if (largeSkinsOnly && smallSkinPart == null) {
				throw new IllegalArgumentException();
			}
		}
		
	}
	
	private static enum SkinPartOverlay {
		HEAD_TOP(40, 0),
		HEAD_BOTTOM(48, 0),
		HEAD_RIGHT(32, 8),
		HEAD_FRONT(40, 8),
		HEAD_LEFT(48, 8),
		HEAD_BACK(56, 8),
		// below: skin parts available in MC 1.8 for large 64x64 skins
		LEG_RIGHT_TOP(4, 32),
		LEG_RIGHT_BOTTOM(8, 32),
		LEG_RIGHT_OUTSIDE(0, 36),
		LEG_RIGHT_FRONT(4, 36),
		LEG_RIGHT_INSIDE(8, 36),
		LEG_RIGHT_BACK(12, 36),
		BODY_TOP(20, 32),
		BODY_BOTTOM(28, 32),
		BODY_RIGHT(16, 36),
		BODY_FRONT(20, 36),
		BODY_LEFT(28, 36),
		BODY_BACK(32, 36),
		ARM_RIGHT_TOP(44, 16),
		ARM_RIGHT_BOTTOM(48, 16),
		ARM_RIGHT_OUTSIDE(40, 20),
		ARM_RIGHT_FRONT(44, 20),
		ARM_RIGHT_INSIDE(48, 20),
		ARM_RIGHT_BACK(52, 20),
		LEG_LEFT_TOP(4, 48),
		LEG_LEFT_BOTTOM(8, 48),
		LEG_LEFT_OUTSIDE(0, 52),
		LEG_LEFT_FRONT(4, 52),
		LEG_LEFT_INSIDE(8, 52),
		LEG_LEFT_BACK(12, 52),
		ARM_LEFT_TOP(52, 48),
		ARM_LEFT_BOTTOM(56, 48),
		ARM_LEFT_OUTSIDE(48, 52),
		ARM_LEFT_FRONT(52, 52),
		ARM_LEFT_INSIDE(56, 52),
		ARM_LEFT_BACK(60, 52);
		
		private final int x, y;
		
		private SkinPartOverlay(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
	}
	
	private static enum SlimSkinPart {
		ARM_RIGHT_TOP(44, 16, 3, 4),
		ARM_RIGHT_BOTTOM(47, 16, 3, 4),
		ARM_RIGHT_FRONT(44, 20, 3, 12),
		ARM_RIGHT_BACK(51, 20, 3, 12),
		ARM_LEFT_TOP(36, 48, 3, 4),
		ARM_LEFT_BOTTOM(39, 48, 3, 4),
		ARM_LEFT_FRONT(36, 52, 3, 12),
		ARM_LEFT_BACK(43, 52, 3, 12);
		
		private final ImageArea area;
		private final ImageArea overlayArea;
		
		private SlimSkinPart(int x, int y, int w, int h) {
			area = new ImageArea(x, y, w, h);
			
			SlimSkinPartOverlay overlay = SlimSkinPartOverlay.valueOf(name());
			overlayArea = new ImageArea(overlay.x, overlay.y, w, h);
		}
	}
	
	private static enum SlimSkinPartOverlay {
		ARM_RIGHT_TOP(44, 16),
		ARM_RIGHT_BOTTOM(47, 16),
		ARM_RIGHT_FRONT(44, 20),
		ARM_RIGHT_BACK(51, 20),
		ARM_LEFT_TOP(52, 48),
		ARM_LEFT_BOTTOM(55, 48),
		ARM_LEFT_FRONT(52, 52),
		ARM_LEFT_BACK(59, 52);
		
		private final int x, y;
		
		private SlimSkinPartOverlay(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
	}
	
	
	private static class ImageArea {
		
		private final int x, y, w, h;
		
		private ImageArea(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
		
	}
	
	
	/**
	 * A simple interface describing a set of {@link PartPosition}'s which can be combined to make an
	 * image of a skin position when used with {@link MinecraftSkinUtil#getPlayerSkinPosition(MinecraftProfile, SkinPosition, int)}
	 * @author Jon
	 */
	public static interface SkinPosition {
		
		/**
		 * The default implementation will calculate the image width by iterating
		 * through {@link #getPartPositions()} and finding the max X coordinate
		 * @param slim - Whether or not the returned image width should be for slim or normal skin types
		 * @return the width the created image should have for this position
		 */
		default int getImageWitdh(boolean slim) {
			return getPartPositions().stream().mapToInt((pos) -> pos.getMaxX(slim)).max().getAsInt();
		}
		
		/**
		 * The default implementation will calculate the image height by iterating
		 * through {@link #getPartPositions()} and finding the max Y coordinate
		 * @param slim - Whether or not the returned image height should be for slim or normal skin types
		 * @return the height the created image should have for this position
		 */
		default int getImageHeight(boolean slim) {
			return getPartPositions().stream().mapToInt((pos) -> pos.getMaxY(slim)).max().getAsInt();
		}
		
		/**
		 * @return A List containing the {@link PartPosition}'s to create this position when combined in a single image
		 */
		List<PartPosition> getPartPositions();
		
	}
	
	/**
	 * A class that describes the orientation of a single {@link SkinPart} among others in a {@link SkinPosition}
	 * @author Jon
	 */
	public static class PartPosition {
		
		private final SkinPart part;
		private final int offsetX, offsetY;
		private final int slimOffsetX, slimOffsetY;
		
		/**
		 * Equivalent to {@link #PartPosition(SkinPart part, int offsetX, int offsetY, int slimOffsetX, int slimOffsetY)
		 * PartPosition(part, offsetX, offsetY, offsetX, offsetY)} Where the same offset values are used for slim skin types.
		 * @param part - The SkinPart being positioned
		 * @param offsetX - The x offset from [0, 0] of the SkinPosition image for normal skin types
		 * @param offsetY - The y offset from [0, 0] of the SkinPosition image for normal skin types
		 */
		public PartPosition(SkinPart part, int offsetX, int offsetY) {
			this(part, offsetX, offsetY, offsetX, offsetY);
		}
		
		/**
		 * @param part - The SkinPart being positioned
		 * @param offsetX - The x offset from [0, 0] of the SkinPosition image for normal skin types
		 * @param offsetY - The y offset from [0, 0] of the SkinPosition image for normal skin types
		 * @param slimOffsetX - The x offset from [0, 0] of the SkinPosition image for slim skin types
		 * @param slimOffsetY - The y offset from [0, 0] of the SkinPosition image for slim skin types
		 */
		public PartPosition(SkinPart part, int offsetX, int offsetY, int slimOffsetX, int slimOffsetY) {
			this.part = part;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.slimOffsetX = slimOffsetX;
			this.slimOffsetY = slimOffsetY;
		}
		
		private int getOffsetX(boolean slim) {
			return slim ? slimOffsetX : offsetX;
		}
		
		private int getOffsetY(boolean slim) {
			return slim ? slimOffsetY : offsetY;
		}
		
		private int getMaxX(boolean slim) {
			return getOffsetX(slim) + getImageArea(slim).w;
		}
		
		private int getMaxY(boolean slim) {
			return getOffsetY(slim) + getImageArea(slim).h;
		}
		
		private ImageArea getImageArea(boolean slim) {
			return slim && part.slimSkinPart != null ? part.slimSkinPart.area : part.area;
		}
		
	}
	
	private static enum DefaultSkinPosition implements SkinPosition {
		FRONT(
				new PartPosition(SkinPart.HEAD_FRONT, 4, 0, 3, 0),
				new PartPosition(SkinPart.ARM_LEFT_FRONT, 12, 8, 11, 8),
				new PartPosition(SkinPart.ARM_RIGHT_FRONT, 0, 8),
				new PartPosition(SkinPart.BODY_FRONT, 4, 8, 3, 8),
				new PartPosition(SkinPart.LEG_LEFT_FRONT, 8, 20, 7, 20),
				new PartPosition(SkinPart.LEG_RIGHT_FRONT, 4, 20, 3, 20)),
		BACK(
				new PartPosition(SkinPart.HEAD_BACK, 4, 0, 3, 0),
				new PartPosition(SkinPart.ARM_LEFT_BACK, 0, 8),
				new PartPosition(SkinPart.ARM_RIGHT_BACK, 12, 8, 11, 8),
				new PartPosition(SkinPart.BODY_BACK, 4, 8, 3, 8),
				new PartPosition(SkinPart.LEG_LEFT_BACK, 4, 20, 3, 20),
				new PartPosition(SkinPart.LEG_RIGHT_BACK, 8, 20, 7, 20)),
		LEFT(
				new PartPosition(SkinPart.HEAD_LEFT, 0, 0),
				new PartPosition(SkinPart.ARM_LEFT_OUTSIDE, 2, 8),
				new PartPosition(SkinPart.LEG_LEFT_OUTSIDE, 2, 20)),
		RIGHT(
				new PartPosition(SkinPart.HEAD_RIGHT, 0, 0),
				new PartPosition(SkinPart.ARM_RIGHT_OUTSIDE, 2, 8),
				new PartPosition(SkinPart.LEG_RIGHT_OUTSIDE, 2, 20)),
		TOP(
				new PartPosition(SkinPart.HEAD_TOP, 4, 0, 3, 0),
				new PartPosition(SkinPart.ARM_LEFT_TOP, 0, 2),
				new PartPosition(SkinPart.ARM_RIGHT_TOP, 12, 2, 11, 2)),
		BOTTOM(
				new PartPosition(SkinPart.HEAD_BOTTOM, 4, 0, 3, 0),
				new PartPosition(SkinPart.ARM_LEFT_BOTTOM, 0, 2),
				new PartPosition(SkinPart.ARM_RIGHT_BOTTOM, 12, 2, 11, 2),
				new PartPosition(SkinPart.LEG_LEFT_BOTTOM, 4, 2, 3, 2),
				new PartPosition(SkinPart.LEG_RIGHT_BOTTOM, 8, 2, 7, 2));
		
		private final List<PartPosition> partPositions;
		
		private final int imageWidth, imageHeight, slimImageWidth, slimImageHeight;
		
		private DefaultSkinPosition(PartPosition... partPositions) {
			this.partPositions = Arrays.asList(partPositions);
			
			imageWidth = SkinPosition.super.getImageWitdh(false);
			imageHeight = SkinPosition.super.getImageHeight(false);
			slimImageWidth = SkinPosition.super.getImageWitdh(true);
			slimImageHeight = SkinPosition.super.getImageHeight(true);
		}
		
		@Override
		public int getImageWitdh(boolean slim) {
			return slim ? slimImageWidth : imageWidth;
		}
		
		@Override
		public int getImageHeight(boolean slim) {
			return slim ? slimImageHeight : imageHeight;
		}
		
		@Override
		public List<PartPosition> getPartPositions() {
			return partPositions;
		}
		
	}
	
}
