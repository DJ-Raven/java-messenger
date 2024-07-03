const blurhash = require("blurhash");
const sharp = require("sharp");
const fs = require("fs");
const path = require("path");

const music_extensions = [".mp3"];

exports.getImageInfo = (image) => {
  return new Promise((resolve, rejects) => {
    sharp(image).metadata((err, metadata) => {
      if (err) return rejects(err);
      sharp(image)
        .raw()
        .ensureAlpha()
        .resize({ width: Math.min(240, metadata.width) })
        .toBuffer((err, buffer, { width, height }) => {
          if (err) return rejects(err);
          const hash = blurhash.encode(
            new Uint8ClampedArray(buffer),
            width,
            height,
            4,
            3
          );
          resolve(
            JSON.stringify({
              width,
              height,
              hash,
            })
          );
        });
    });
  });
};

exports.isMusicFile = (file) => {
  const file_extension = path.extname(file).toLowerCase();
  return music_extensions.includes(file_extension);
};
