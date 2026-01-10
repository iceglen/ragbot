#!/usr/bin/env python3
import argparse
import os
import shutil
from pathlib import Path


def sync_dirs(src: Path, dst: Path) -> None:
    if not src.is_dir():
        raise ValueError(f"src is not a directory: {src}")
    if not dst.is_dir():
        raise ValueError(f"dst is not a directory: {dst}")

    # Список файлов только верхнего уровня
    src_files = {p.name for p in src.iterdir() if p.is_file()}
    dst_files = {p.name for p in dst.iterdir() if p.is_file()}

    # Файлы, которые есть в dst, но нет в src
    extra_in_dst = dst_files - src_files

    if not extra_in_dst:
        return

    removed_dir = dst.parent / "removed"
    removed_dir.mkdir(exist_ok=True)

    for filename in extra_in_dst:
        src_path = dst / filename
        dst_path = removed_dir / filename
        # На всякий случай проверяем, что это файл
        if src_path.is_file():
            shutil.move(str(src_path), str(dst_path))


def main():
    parser = argparse.ArgumentParser(
        description="Синхронизация имен файлов между src и dst (лишние из dst -> removed)."
    )
    parser.add_argument("src", type=Path, help="Путь до src директории")
    parser.add_argument("dst", type=Path, help="Путь до dst директории")

    args = parser.parse_args()
    sync_dirs(args.src, args.dst)


if __name__ == "__main__":
    main()
